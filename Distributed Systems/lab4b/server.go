package shardkv

import (
	"../shardmaster"
	"bytes"
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

type GiveShardArgs struct {
	ConfigNum int
	Shard     int
	KvTable   map[string]string
}

type GiveShardReply struct {
	Err Err
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

	kvTable            map[int]map[string]string
	dead               int32
	isLeader           bool
	openRequests       map[int64]chan bool
	executedRequests   map[int64]bool
	config             shardmaster.Config
	submittedConfigNum int
	newConfigKvTable   map[int]map[string]string

	lastExecutedIndex int
	snapshotIndex     int

	mck *shardmaster.Clerk
}

func (kv *ShardKV) Get(args *GetArgs, reply *GetReply) {
	shard := key2shard(args.Key)
	DPrintf("%v-%v: Received Get request for shard %d\n", kv.gid, kv.me, shard)
	kv.mu.Lock()
	DPrintf("%v-%v: Shard config num %v %v\n", kv.gid, kv.me, kv.config.Num, kv.config.Shards)
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

func (kv *ShardKV) GiveShard(args *GiveShardArgs, reply *GiveShardReply) {
	DPrintf("%v-%v: Received GiveShard request for shard %d\n", kv.gid, kv.me, args.Shard)
	reply.Err = OK
	kv.mu.Lock()
	if !kv.isLeader {
		DPrintf("%v-%v: Not leader\n", kv.gid, kv.me)
		reply.Err = ErrWrongLeader
		kv.mu.Unlock()
		return
	}
	if args.ConfigNum != kv.config.Num+1 {
		DPrintf("%v-%v: Wrong config num, expected %v actual %v\n",
			kv.gid, kv.me, kv.config.Num+1, args.ConfigNum)
		reply.Err = ErrWrongConfigNum
		kv.mu.Unlock()
		return
	}
	kv.newConfigKvTable[args.Shard] = args.KvTable
	DPrintf("%v-%v: Saved shard %v\n", kv.gid, kv.me, args.Shard)
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
	kv.newConfigKvTable = make(map[int]map[string]string)
	kv.lastExecutedIndex = 0
	kv.snapshotIndex = 0

	kv.mck = shardmaster.MakeClerk(kv.masters)

	kv.applyCh = make(chan raft.ApplyMsg)
	kv.rf = raft.Make(servers, me, persister, kv.applyCh)

	kv.restoreSnapshot(kv.rf.GetSavedSnapshot())

	go kv.applyChProcessor()
	go kv.configurationChangeHandler()
	go kv.leadershipChangeHandler()
	if maxraftstate != -1 {
		go kv.maxRaftStateHandler()
	}

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
					kv.newConfigKvTable = make(map[int]map[string]string)
					kv.config = op.Config
				}
			}
			DPrintf("%v-%v:%v: kv table after %v\n", kv.gid, kv.me, op.RequestId, kv.kvTable)
			kv.executedRequests[op.RequestId] = true
			kv.lastExecutedIndex = msg.CommandIndex
			if _, ok := kv.openRequests[op.RequestId]; ok {
				kv.signalRequestCh(op.RequestId, true)
			}
		} else if msg.IsSnapshot {
			kv.restoreSnapshot(msg.Data)
			kv.lastExecutedIndex = msg.CommandIndex
		}
		kv.mu.Unlock()
	}
}

func (kv *ShardKV) configurationChangeHandler() {
	for {
		if kv.killed() {
			return
		}
		newConfig := kv.mck.Query(kv.config.Num + 1)
		kv.mu.Lock()
		if kv.isLeader && newConfig.Num == kv.config.Num+1 && newConfig.Num != kv.submittedConfigNum {
			DPrintf("%v-%v: Apply new shard config num %v %v, old shard config: %v\n",
				kv.gid, kv.me, newConfig.Num, newConfig.Shards, kv.config.Shards)
			for shard, gid := range newConfig.Shards {
				prevOwner := kv.config.Shards[shard]
				if kv.gid == gid && prevOwner == 0 {
					// First owner
					kv.newConfigKvTable[shard] = make(map[string]string)
				} else if kv.gid == prevOwner && kv.gid != gid {
					// Lose shard
					kv.giveShard(newConfig.Num, newConfig.Groups, gid, shard)
					// Stop serving immediately
					kv.config.Shards[shard] = 0
				} else if kv.gid == gid && kv.gid != prevOwner {
					// Gain shard
					for {
						DPrintf("%v-%v: Wait for shard %v\n", kv.gid, kv.me, shard)
						if _, ok := kv.newConfigKvTable[shard]; ok {
							DPrintf("%v-%v: Received shard %v\n", kv.gid, kv.me, shard)
							break
						}
						kv.mu.Unlock()
						// Wait until shard received
						time.Sleep(HouseKeepersSleepTime)
						kv.mu.Lock()
					}
				}
			}
			newConfigKvTable := make(map[int]map[string]string)
			for shard, kvTable := range kv.newConfigKvTable {
				newConfigKvTable[shard] = copyKvTable(kvTable)
			}
			kv.mu.Unlock()
			index, term, _ := kv.rf.Start(Op{RequestId: nrand(), Name: "ApplyNewConfig", Config: newConfig, KvTable: newConfigKvTable})
			DPrintf("%v-%v: Submitted ApplyNewConfig for index %v term %v\n", kv.gid, kv.me, index, term)
			kv.mu.Lock()
			kv.submittedConfigNum = newConfig.Num
		}
		kv.mu.Unlock()
		time.Sleep(HouseKeepersSleepTime)
	}
}

func (kv *ShardKV) giveShard(configNum int, groups map[int][]string, gid int, shard int) {
	DPrintf("%v-%v: Give shard %v to gid %v\n", kv.gid, kv.me, shard, gid)
	args := GiveShardArgs{ConfigNum: configNum, Shard: shard, KvTable: kv.kvTable[shard]}
	servers := groups[gid]
	for {
		for si := 0; si < len(servers); si++ {
			srv := kv.make_end(servers[si])
			var reply GiveShardReply
			kv.mu.Unlock()
			ok := srv.Call("ShardKV.GiveShard", &args, &reply)
			kv.mu.Lock()
			if ok && reply.Err == OK {
				return
			}
		}
	}
}

func (kv *ShardKV) leadershipChangeHandler() {
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

func (kv *ShardKV) maxRaftStateHandler() {
	for {
		if kv.killed() {
			return
		}
		if kv.rf.GetStateSize() >= kv.maxraftstate {
			kv.mu.Lock()
			if kv.lastExecutedIndex != kv.snapshotIndex {
				DPrintf("%v-%v: Begin to save snapshot at log index %d\n", kv.gid, kv.me, kv.lastExecutedIndex)
				w := new(bytes.Buffer)
				e := labgob.NewEncoder(w)
				e.Encode(kv.kvTable)
				e.Encode(kv.executedRequests)
				e.Encode(kv.lastExecutedIndex)
				e.Encode(kv.config)
				e.Encode(kv.newConfigKvTable)
				data := w.Bytes()
				lastExecutedIndex := kv.lastExecutedIndex
				kv.snapshotIndex = kv.lastExecutedIndex
				kv.mu.Unlock()
				kv.rf.SaveSnapshot(data, lastExecutedIndex)
			} else {
				kv.mu.Unlock()
			}
		}
		time.Sleep(HouseKeepersSleepTime)
	}
}

func (kv *ShardKV) restoreSnapshot(data []byte) {
	if data == nil || len(data) < 1 {
		return
	}
	r := bytes.NewBuffer(data)
	d := labgob.NewDecoder(r)
	var kvTable map[int]map[string]string
	var executedRequests map[int64]bool
	var lastExecutedIndex int
	var config shardmaster.Config
	var newConfigKvTable map[int]map[string]string
	if d.Decode(&kvTable) != nil || d.Decode(&executedRequests) != nil || d.Decode(&lastExecutedIndex) != nil ||
		d.Decode(&config) != nil || d.Decode(&newConfigKvTable) != nil {
		DPrintf("%d: Failed to restore snapshot\n", kv.me)
		return
	} else {
		kv.kvTable = kvTable
		kv.executedRequests = executedRequests
		kv.lastExecutedIndex = lastExecutedIndex
		kv.config = config
		kv.newConfigKvTable = newConfigKvTable
	}
}
