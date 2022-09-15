package shardkv

import (
	"../shardmaster"
	"log"
	"sync/atomic"
	"time"
)
import "../labrpc"
import "../raft"
import "sync"
import "../labgob"

const Debug = 0

func DPrintf(format string, a ...interface{}) (n int, err error) {
	if Debug > 0 {
		log.Printf(format, a...)
	}
	return
}

const HouseKeepersSleepTime = 10 * time.Millisecond

type Op struct {
	LastExecutedRequestId int64
	RequestId             int64
	Name                  string
	Key                   string
	Value                 string
}

type ShardKV struct {
	mu           sync.Mutex
	me           int
	rf           *raft.Raft
	applyCh      chan raft.ApplyMsg
	make_end     func(string) *labrpc.ClientEnd
	gid          int
	masters      []*labrpc.ClientEnd
	maxraftstate int // snapshot if log grows this big

	kvTable          map[string]string
	dead             int32
	isLeader         bool
	openRequests     map[int64]chan bool
	executedRequests map[int64]bool
	knownShardConfig int
	shardsOwned      map[int]bool

	lastExecutedIndex int
	snapshotIndex     int

	mck *shardmaster.Clerk
}

func (kv *ShardKV) Get(args *GetArgs, reply *GetReply) {
	keyShard := key2shard(args.Key)
	DPrintf("%v: Received Get request for shard %d\n", kv.me, keyShard)
	kv.mu.Lock()
	if _, ok := kv.shardsOwned[keyShard]; !ok {
		DPrintf("%d:%v: Wrong shards; owned shards: %v\n", kv.me, args.RequestId, kv.shardsOwned)
		reply.Err = ErrWrongGroup
		kv.mu.Unlock()
		return
	}
	reply.Err = OK
	index, term, isLeader := kv.rf.Start(Op{LastExecutedRequestId: args.LastRequestId,
		RequestId: args.RequestId, Name: "Get", Key: args.Key})
	DPrintf("%v:%v: Submitted for index %d term %d\n", kv.me, args.RequestId, index, term)
	if !isLeader {
		DPrintf("%d:%v: Not leader\n", kv.me, args.RequestId)
		reply.Err = ErrWrongLeader
		kv.mu.Unlock()
		return
	}
	kv.isLeader = true
	kv.openRequests[args.RequestId] = make(chan bool)
	requestCh := kv.openRequests[args.RequestId]
	kv.mu.Unlock()
	ok := <-requestCh
	kv.mu.Lock()
	if !ok {
		DPrintf("%d:%v: Failed to process request\n", kv.me, args.RequestId)
		reply.Err = ErrWrongLeader
		kv.mu.Unlock()
		return
	}
	DPrintf("%d:%v: OK\n", kv.me, args.RequestId)
	reply.Value = kv.kvTable[args.Key]
	kv.mu.Unlock()
	return
}

func (kv *ShardKV) PutAppend(args *PutAppendArgs, reply *PutAppendReply) {
	keyShard := key2shard(args.Key)
	DPrintf("%v: Received PutAppend request for shard %d\n", kv.me, keyShard)
	kv.mu.Lock()
	if _, ok := kv.shardsOwned[keyShard]; !ok {
		DPrintf("%d:%v: Wrong shards; owned shards: %v\n", kv.me, args.RequestId, kv.shardsOwned)
		reply.Err = ErrWrongGroup
		kv.mu.Unlock()
		return
	}
	reply.Err = OK
	index, term, isLeader := kv.rf.Start(Op{LastExecutedRequestId: args.LastRequestId,
		RequestId: args.RequestId, Name: args.Op, Key: args.Key, Value: args.Value})
	DPrintf("%v:%v: Submitted for index %d term %d\n", kv.me, args.RequestId, index, term)
	if !isLeader {
		DPrintf("%d:%v: Not leader\n", kv.me, args.RequestId)
		reply.Err = ErrWrongLeader
		kv.mu.Unlock()
		return
	}
	kv.isLeader = true
	kv.openRequests[args.RequestId] = make(chan bool)
	requestCh := kv.openRequests[args.RequestId]
	kv.mu.Unlock()
	ok := <-requestCh
	kv.mu.Lock()
	if !ok {
		DPrintf("%d:%v: Failed to process request\n", kv.me, args.RequestId)
		reply.Err = ErrWrongLeader
		kv.mu.Unlock()
		return
	}
	DPrintf("%d:%v: OK\n", kv.me, args.RequestId)
	kv.mu.Unlock()
	return
}

//
// the tester calls Kill() when a ShardKV instance won't
// be needed again. you are not required to do anything
// in Kill(), but it might be convenient to (for example)
// turn off debug output from this instance.
//
func (kv *ShardKV) Kill() {
	kv.rf.Kill()
	atomic.StoreInt32(&kv.dead, 1)
}

func (kv *ShardKV) killed() bool {
	z := atomic.LoadInt32(&kv.dead)
	return z == 1
}

//
// servers[] contains the ports of the servers in this group.
//
// me is the index of the current server in servers[].
//
// the k/v server should store snapshots through the underlying Raft
// implementation, which should call persister.SaveStateAndSnapshot() to
// atomically save the Raft state along with the snapshot.
//
// the k/v server should snapshot when Raft's saved state exceeds
// maxraftstate bytes, in order to allow Raft to garbage-collect its
// log. if maxraftstate is -1, you don't need to snapshot.
//
// gid is this group's GID, for interacting with the shardmaster.
//
// pass masters[] to shardmaster.MakeClerk() so you can send
// RPCs to the shardmaster.
//
// make_end(servername) turns a server name from a
// Config.Groups[gid][i] into a labrpc.ClientEnd on which you can
// send RPCs. You'll need this to send RPCs to other groups.
//
// look at client.go for examples of how to use masters[]
// and make_end() to send RPCs to the group owning a specific shard.
//
// StartServer() must return quickly, so it should start goroutines
// for any long-running work.
//
func StartServer(servers []*labrpc.ClientEnd, me int, persister *raft.Persister, maxraftstate int, gid int,
	masters []*labrpc.ClientEnd, make_end func(string) *labrpc.ClientEnd) *ShardKV {
	// call labgob.Register on structures you want
	// Go's RPC library to marshall/unmarshall.
	labgob.Register(Op{})

	kv := new(ShardKV)
	kv.me = me
	kv.maxraftstate = maxraftstate
	kv.make_end = make_end
	kv.gid = gid
	kv.masters = masters

	kv.kvTable = make(map[string]string)
	kv.openRequests = make(map[int64]chan bool)
	kv.executedRequests = make(map[int64]bool)
	kv.knownShardConfig = 0
	kv.shardsOwned = make(map[int]bool)
	kv.lastExecutedIndex = 0
	kv.snapshotIndex = 0

	kv.mck = shardmaster.MakeClerk(kv.masters)

	kv.applyCh = make(chan raft.ApplyMsg)
	kv.rf = raft.Make(servers, me, persister, kv.applyCh)

	go kv.applyChProcessor()
	go kv.configurationChangeDetector()
	go kv.leadershipChangeDetector()

	return kv
}

func (kv *ShardKV) applyChProcessor() {
	for {
		if kv.killed() {
			return
		}
		msg := <-kv.applyCh
		kv.mu.Lock()
		if msg.CommandValid {
			op := msg.Command.(Op)
			DPrintf("%d:%v: Received %v applyMsg %v\n", kv.me, op.RequestId, op.Name, msg)
			if kv.executedRequests[op.RequestId] {
				DPrintf("%d:%v: Request already executed\n", kv.me, op.RequestId)
				kv.mu.Unlock()
				return
			}
			if op.LastExecutedRequestId != 0 {
				delete(kv.executedRequests, op.LastExecutedRequestId)
			}
			switch op.Name {
			case "Put":
				kv.kvTable[op.Key] = op.Value
			case "Append":
				kv.kvTable[op.Key] = kv.kvTable[op.Key] + op.Value
			}
			kv.executedRequests[op.RequestId] = true
			kv.lastExecutedIndex = msg.CommandIndex
			if _, ok := kv.openRequests[op.RequestId]; ok {
				kv.signalRequestCh(op.RequestId, true)
			}
		}
		kv.mu.Unlock()
	}
}

func (kv *ShardKV) configurationChangeDetector() {
	for {
		if kv.killed() {
			return
		}
		shardConfig := kv.mck.Query(-1)
		kv.mu.Lock()
		if shardConfig.Num > kv.knownShardConfig {
			DPrintf("%v: Detected new shard config %v, old shards owned: %v\n", kv.me, shardConfig.Shards, kv.shardsOwned)
			kv.shardsOwned = make(map[int]bool)
			for shard, gid := range shardConfig.Shards {
				if gid == kv.gid {
					kv.shardsOwned[shard] = true
				}
			}
			kv.knownShardConfig = shardConfig.Num
			DPrintf("%v: Detected new shard config, new shards owned: %v\n", kv.me, kv.shardsOwned)
		}
		kv.mu.Unlock()
		time.Sleep(HouseKeepersSleepTime)
	}
}

func (kv *ShardKV) leadershipChangeDetector() {
	for {
		if kv.killed() {
			return
		}
		_, stillLeader := kv.rf.GetState()
		kv.mu.Lock()
		if kv.isLeader {
			if !stillLeader {
				DPrintf("%v: Lost leadership\n", kv.me)
				kv.closeOpenRequests()
			}
		}
		kv.mu.Unlock()
		time.Sleep(HouseKeepersSleepTime)
	}
}

func (kv *ShardKV) closeOpenRequests() {
	for requestId := range kv.openRequests {
		kv.signalRequestCh(requestId, false)
	}
}

func (kv *ShardKV) signalRequestCh(requestId int64, value bool) {
	DPrintf("%v:%v: Signal %v to open request channel\n", kv.me, requestId, value)
	kv.openRequests[requestId] <- value
	delete(kv.openRequests, requestId)
}
