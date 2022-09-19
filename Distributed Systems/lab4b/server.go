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

	Shard int
	Key   string
	Value string

	Config  shardmaster.Config
	KvTable map[int]map[string]string
}

type RequestShardArgs struct {
	Shard int
}

type RequestShardReply struct {
	ShardKvTable map[string]string
	Err          Err
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

	kvTable          map[int]map[string]string
	dead             int32
	isLeader         bool
	openRequests     map[int64]chan bool
	executedRequests map[int64]bool
	config           shardmaster.Config

	lastExecutedIndex int
	snapshotIndex     int

	mck *shardmaster.Clerk
}

func (kv *ShardKV) Get(args *GetArgs, reply *GetReply) {
	shard := key2shard(args.Key)
	DPrintf("%v-%v: Received Get request for shard %d\n", kv.gid, kv.me, shard)
	kv.mu.Lock()
	DPrintf("%v-%v: Shard config %v\n", kv.gid, kv.me, kv.config.Shards)
	if !kv.isLeader {
		DPrintf("%v-%v:%v: Not leader\n", kv.gid, kv.me, args.RequestId)
		reply.Err = ErrWrongLeader
		kv.mu.Unlock()
		return
	}
	if kv.gid != kv.config.Shards[shard] {
		DPrintf("%v-%v:%v: ShardKvTable not owned\n", kv.gid, kv.me, args.RequestId)
		reply.Err = ErrWrongGroup
		kv.mu.Unlock()
		return
	}
	reply.Err = OK
	index, term, _ := kv.rf.Start(Op{LastExecutedRequestId: args.LastRequestId,
		RequestId: args.RequestId, Name: "Get", Shard: shard, Key: args.Key})
	DPrintf("%v-%v:%v: Submitted for index %d term %d\n", kv.gid, kv.me, args.RequestId, index, term)
	kv.isLeader = true
	kv.openRequests[args.RequestId] = make(chan bool)
	requestCh := kv.openRequests[args.RequestId]
	kv.mu.Unlock()
	ok := <-requestCh
	kv.mu.Lock()
	if !ok {
		DPrintf("%v-%v:%v: Failed to process request\n", kv.gid, kv.me, args.RequestId)
		reply.Err = ErrWrongLeader
		kv.mu.Unlock()
		return
	}
	if kv.gid != kv.config.Shards[shard] {
		DPrintf("%v-%v:%v: ShardKvTable not owned\n", kv.gid, kv.me, args.RequestId)
		reply.Err = ErrWrongGroup
		kv.mu.Unlock()
		return
	}
	DPrintf("%v-%v:%v: OK\n", kv.gid, kv.me, args.RequestId)
	reply.Value = kv.kvTable[shard][args.Key]
	kv.mu.Unlock()
	return
}

func (kv *ShardKV) PutAppend(args *PutAppendArgs, reply *PutAppendReply) {
	shard := key2shard(args.Key)
	DPrintf("%v-%v: Received PutAppend request for shard %d\n", kv.gid, kv.me, shard)
	kv.mu.Lock()
	if !kv.isLeader {
		DPrintf("%v-%v:%v: Not leader\n", kv.gid, kv.me, args.RequestId)
		reply.Err = ErrWrongLeader
		kv.mu.Unlock()
		return
	}
	if kv.gid != kv.config.Shards[shard] {
		DPrintf("%v-%v:%v: ShardKvTable not owned\n", kv.gid, kv.me, args.RequestId)
		reply.Err = ErrWrongGroup
		kv.mu.Unlock()
		return
	}
	reply.Err = OK
	index, term, _ := kv.rf.Start(Op{LastExecutedRequestId: args.LastRequestId,
		RequestId: args.RequestId, Name: args.Op, Shard: shard, Key: args.Key, Value: args.Value})
	DPrintf("%v-%v:%v: Submitted for index %d term %d\n", kv.gid, kv.me, args.RequestId, index, term)
	kv.isLeader = true
	kv.openRequests[args.RequestId] = make(chan bool)
	requestCh := kv.openRequests[args.RequestId]
	kv.mu.Unlock()
	ok := <-requestCh
	kv.mu.Lock()
	if !ok {
		DPrintf("%v-%v:%v: Failed to process request\n", kv.gid, kv.me, args.RequestId)
		reply.Err = ErrWrongLeader
		kv.mu.Unlock()
		return
	}
	if kv.gid != kv.config.Shards[shard] {
		DPrintf("%v-%v:%v: ShardKvTable not owned\n", kv.gid, kv.me, args.RequestId)
		reply.Err = ErrWrongGroup
		kv.mu.Unlock()
		return
	}
	DPrintf("%v-%v:%v: OK\n", kv.gid, kv.me, args.RequestId)
	kv.mu.Unlock()
	return
}

func (kv *ShardKV) RequestShard(args *RequestShardArgs, reply *RequestShardReply) {
	DPrintf("%v-%v: Received RequestShard request for shard %d\n", kv.gid, kv.me, args.Shard)
	reply.Err = OK
	kv.mu.Lock()
	if !kv.isLeader {
		DPrintf("%v-%v: Not leader\n", kv.gid, kv.me)
		reply.Err = ErrWrongLeader
		kv.mu.Unlock()
		return
	}
	reply.ShardKvTable = kv.kvTable[args.Shard]
	kv.config.Shards[args.Shard] = 0 // stop serving immediately
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

	kv.kvTable = make(map[int]map[string]string)
	kv.openRequests = make(map[int64]chan bool)
	kv.executedRequests = make(map[int64]bool)
	kv.config = shardmaster.Config{Num: 0}
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
			DPrintf("%v-%v:%v: Received %v applyMsg %v\n", kv.gid, kv.me, op.RequestId, op.Name, msg)
			if kv.executedRequests[op.RequestId] {
				DPrintf("%v-%v:%v: Request already executed\n", kv.gid, kv.me, op.RequestId)
				kv.mu.Unlock()
				return
			}
			if op.LastExecutedRequestId != 0 {
				delete(kv.executedRequests, op.LastExecutedRequestId)
			}
			switch op.Name {
			case "Put":
				kv.kvTable[op.Shard][op.Key] = op.Value
			case "Append":
				kv.kvTable[op.Shard][op.Key] = kv.kvTable[op.Shard][op.Key] + op.Value
			case "ApplyNewConfig":
				// The following check is necessary to guard against a few cases where
				// multiple new config apply messages for the same config are submitted
				// into Raft log.
				// - The most obvious case is where a leader submits the message for config
				//   number N but soon checks again for the same config. This could be
				//   mitigated by remembering which apply configs have been submitted.
				// - The trickier case is when there is a leadership change while apply
				//   messages are pending. There is no straightforward way to transfer the
				//   submitted state from old leader to new.
				if op.Config.Num == kv.config.Num+1 {
					for shard, kvTable := range op.KvTable {
						kv.kvTable[shard] = copyKvTable(kvTable)
					}
					kv.config = op.Config
				}
			}
			DPrintf("%v-%v:%v: kv table after %v\n", kv.gid, kv.me, op.RequestId, kv.kvTable)
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
		newConfig := kv.mck.Query(kv.config.Num + 1)
		kv.mu.Lock()
		if kv.isLeader && newConfig.Num == kv.config.Num+1 {
			DPrintf("%v-%v: Apply new shard config num %v %v, old shard config: %v\n",
				kv.gid, kv.me, newConfig.Num, newConfig.Shards, kv.config.Shards)
			kvTableToApply := kv.prepKvTableToApply(newConfig)
			DPrintf("%v-%v: kv table to be applied %v\n", kv.gid, kv.me, kvTableToApply)
			kv.mu.Unlock()
			requestId := nrand()
			index, term, _ := kv.rf.Start(Op{RequestId: requestId, Name: "ApplyNewConfig",
				Config: newConfig, KvTable: kvTableToApply})
			DPrintf("%v-%v: Submit ApplyNewConfig %v index %v term %d\n", kv.gid, kv.me, requestId, index, term)
		} else {
			kv.mu.Unlock()
		}
		time.Sleep(HouseKeepersSleepTime)
	}
}

func (kv *ShardKV) prepKvTableToApply(newConfig shardmaster.Config) map[int]map[string]string {
	kvTableToApply := make(map[int]map[string]string)
	for shard, gid := range newConfig.Shards {
		prevOwner := kv.config.Shards[shard]
		if kv.gid == gid && prevOwner == 0 {
			// New shard without previous owner
			kvTableToApply[shard] = make(map[string]string)
		} else if kv.gid == gid && prevOwner != kv.gid {
			// New shard with previous owner
			kvTableToApply[shard] = kv.requestShard(prevOwner, shard)
		}
	}
	return kvTableToApply
}

func (kv *ShardKV) requestShard(gid int, shard int) map[string]string {
	args := RequestShardArgs{Shard: shard}
	if servers, ok := kv.config.Groups[gid]; ok {
		for si := 0; si < len(servers); si++ {
			srv := kv.make_end(servers[si])
			var reply RequestShardReply
			ok := srv.Call("ShardKV.RequestShard", &args, &reply)
			if ok && reply.Err == OK {
				return reply.ShardKvTable
			}
		}
	}
	return make(map[string]string) // unreachable
}

func (kv *ShardKV) leadershipChangeDetector() {
	for {
		if kv.killed() {
			return
		}
		_, stillLeader := kv.rf.GetState()
		kv.mu.Lock()
		if !kv.isLeader && stillLeader {
			kv.isLeader = true
		} else if kv.isLeader && !stillLeader {
			DPrintf("%v-%v: Lost leadership\n", kv.gid, kv.me)
			kv.closeOpenRequests()
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
	DPrintf("%v-%v:%v: Signal %v to open request channel\n", kv.gid, kv.me, requestId, value)
	kv.openRequests[requestId] <- value
	delete(kv.openRequests, requestId)
}

func copyKvTable(src map[string]string) map[string]string {
	dst := make(map[string]string)
	for k, v := range src {
		dst[k] = v
	}
	return dst
}
