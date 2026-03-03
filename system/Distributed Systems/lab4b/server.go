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

const Debug = 1

func DPrintf(format string, a ...interface{}) (n int, err error) {
	if Debug > 0 {
		log.Printf(format, a...)
	}
	return
}

const HouseKeepersSleepTime = 100 * time.Millisecond

type Op struct {
	LastExecutedRequestId int64
	RequestId             int64
	Name                  string

	Shard int
	Key   string
	Value string

	Config       shardmaster.Config
	KvTable      map[int]map[string]string
	ShardKvTable map[string]string
}

type GiveShardArgs struct {
	RequestId    int64
	Config       shardmaster.Config
	Shard        int
	ShardKvTable map[string]string
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

	kvTable           map[int]map[string]string
	dead              int32
	isLeader          bool
	openRequests      map[int64]chan bool
	executedRequests  map[int64]bool
	config            shardmaster.Config
	savedShardKvTable map[int]map[int]map[string]string
	isReconfiguring   bool

	lastExecutedIndex int
	snapshotIndex     int

	mck *shardmaster.Clerk
}

func (kv *ShardKV) Get(args *GetArgs, reply *GetReply) {
	shard := key2shard(args.Key)
	DPrintf("%v-%vc%v: Received Get request for shard %v\n", kv.gid, kv.me, kv.config.Num, shard)
	kv.mu.Lock()
	if !kv.isLeader {
		DPrintf("%v-%vc%v:%v: Not leader\n", kv.gid, kv.me, kv.config.Num, args.RequestId)
		reply.Err = ErrWrongLeader
		kv.mu.Unlock()
		return
	}
	if kv.gid != kv.config.Shards[shard] || kv.isReconfiguring {
		DPrintf("%v-%vc%v:%v: ShardKvTable not owned\n", kv.gid, kv.me, kv.config.Num, args.RequestId)
		reply.Err = ErrWrongGroup
		kv.mu.Unlock()
		return
	}
	reply.Err = OK
	index, term, _ := kv.rf.Start(Op{LastExecutedRequestId: args.LastRequestId,
		RequestId: args.RequestId, Name: "Get", Shard: shard, Key: args.Key})
	DPrintf("%v-%vc%v:%v: Submitted for index %d term %d\n", kv.gid, kv.me, kv.config.Num, args.RequestId, index, term)
	kv.isLeader = true
	kv.openRequests[args.RequestId] = make(chan bool)
	requestCh := kv.openRequests[args.RequestId]
	kv.mu.Unlock()
	ok := <-requestCh
	kv.mu.Lock()
	if !ok || !kv.isLeader {
		DPrintf("%v-%vc%v:%v: Failed to process request\n", kv.gid, kv.me, kv.config.Num, args.RequestId)
		kv.executedRequests[args.RequestId] = false
		reply.Err = ErrWrongLeader
		kv.mu.Unlock()
		return
	}
	if kv.gid != kv.config.Shards[shard] || kv.isReconfiguring {
		DPrintf("%v-%vc%v:%v: ShardKvTable not owned\n", kv.gid, kv.me, kv.config.Num, args.RequestId)
		kv.executedRequests[args.RequestId] = false
		reply.Err = ErrWrongGroup
		kv.mu.Unlock()
		return
	}
	DPrintf("%v-%vc%v:%v: OK\n", kv.gid, kv.me, kv.config.Num, args.RequestId)
	reply.Value = kv.kvTable[shard][args.Key]
	kv.mu.Unlock()
	return
}

func (kv *ShardKV) PutAppend(args *PutAppendArgs, reply *PutAppendReply) {
	shard := key2shard(args.Key)
	DPrintf("%v-%vc%v: Received PutAppend request for shard %d\n", kv.gid, kv.me, kv.config.Num, shard)
	kv.mu.Lock()
	if !kv.isLeader {
		DPrintf("%v-%vc%v:%v: Not leader\n", kv.gid, kv.me, kv.config.Num, args.RequestId)
		reply.Err = ErrWrongLeader
		kv.mu.Unlock()
		return
	}
	if kv.gid != kv.config.Shards[shard] || kv.isReconfiguring {
		DPrintf("%v-%vc%v:%v: ShardKvTable not owned\n", kv.gid, kv.me, kv.config.Num, args.RequestId)
		reply.Err = ErrWrongGroup
		kv.mu.Unlock()
		return
	}
	reply.Err = OK
	index, term, _ := kv.rf.Start(Op{LastExecutedRequestId: args.LastRequestId,
		RequestId: args.RequestId, Name: args.Op, Shard: shard, Key: args.Key, Value: args.Value})
	DPrintf("%v-%vc%v:%v: Submitted for index %d term %d\n", kv.gid, kv.me, kv.config.Num, args.RequestId, index, term)
	kv.isLeader = true
	kv.openRequests[args.RequestId] = make(chan bool)
	requestCh := kv.openRequests[args.RequestId]
	kv.mu.Unlock()
	ok := <-requestCh
	kv.mu.Lock()
	if !ok || !kv.isLeader {
		DPrintf("%v-%vc%v:%v: Failed to process request\n", kv.gid, kv.me, kv.config.Num, args.RequestId)
		kv.executedRequests[args.RequestId] = false
		reply.Err = ErrWrongLeader
		kv.mu.Unlock()
		return
	}
	if kv.gid != kv.config.Shards[shard] || kv.isReconfiguring {
		DPrintf("%v-%vc%v:%v: ShardKvTable not owned\n", kv.gid, kv.me, kv.config.Num, args.RequestId)
		kv.executedRequests[args.RequestId] = false
		reply.Err = ErrWrongGroup
		kv.mu.Unlock()
		return
	}
	DPrintf("%v-%vc%v:%v: OK\n", kv.gid, kv.me, kv.config.Num, args.RequestId)
	kv.mu.Unlock()
	return
}

func (kv *ShardKV) GiveShard(args *GiveShardArgs, reply *GiveShardReply) {
	DPrintf("%v-%vc%v: Received GiveShard request for shard %v, config num %v\n",
		kv.gid, kv.me, kv.config.Num, args.Shard, args.Config.Num)
	kv.mu.Lock()
	if !kv.isLeader {
		DPrintf("%v-%vc%v: Not leader\n", kv.gid, kv.me, kv.config.Num)
		reply.Err = ErrWrongLeader
		kv.mu.Unlock()
		return
	}
	//if args.Config.Num > kv.config.Num+1 {
	//	DPrintf("%v-%vc%v: Wrong config num, expected %v actual %v\n",
	//		kv.gid, kv.me, kv.config.Num, kv.config.Num+1, args.Config.Num)
	//	reply.Err = ErrWrongConfigNum
	//	kv.mu.Unlock()
	//	return
	//}
	//if args.Config.Num <= kv.config.Num {
	//	DPrintf("%v-%vc%v: Received shard from old config num; ignoring...\n", kv.gid, kv.me, kv.config.Num)
	//	reply.Err = OK
	//	kv.mu.Unlock()
	//	return
	//}
	//DPrintf("%v-%vc%v: Good GiveShard request, attempt to save\n", kv.gid, kv.me, kv.config.Num)
	ok := kv.submitAndWait(Op{RequestId: nrand(), Name: "SaveShard", Config: args.Config,
		Shard: args.Shard, ShardKvTable: args.ShardKvTable})
	if ok {
		reply.Err = OK
	} else {
		reply.Err = ErrWrongLeader
	}
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
	kv.savedShardKvTable = make(map[int]map[int]map[string]string)
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
			DPrintf("%v-%vc%v:%v: Received %v applyMsg %v\n", kv.gid, kv.me, kv.config.Num, op.RequestId, op.Name, msg)
			if kv.executedRequests[op.RequestId] {
				DPrintf("%v-%vc%v:%v: Request already executed\n", kv.gid, kv.me, kv.config.Num, op.RequestId)
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
			case "ReleaseShard":
				kv.config.Shards[op.Shard] = -1
			case "SaveShard":
				if _, ok := kv.savedShardKvTable[op.Config.Num]; !ok {
					kv.savedShardKvTable[op.Config.Num] = make(map[int]map[string]string)
				}
				kv.savedShardKvTable[op.Config.Num][op.Shard] = op.ShardKvTable
			case "ApplyNewConfig":
				if op.Config.Num != kv.config.Num+1 {
					DPrintf("%v-%vc%v:%v: Abnormal ApplyNewConfig message, current config num %v, new config num %v; rejecting...\n",
						kv.gid, kv.me, kv.config.Num, op.RequestId, kv.config.Num, op.Config.Num)
				} else {
					for shard, kvTable := range op.KvTable {
						kv.kvTable[shard] = copyKvTable(kvTable)
					}
					kv.config = op.Config
					kv.isReconfiguring = false
				}
			}
			//DPrintf("%v-%vc%v:%v: kv table after %v\n", kv.gid, kv.me, kv.config.Num, op.RequestId, kv.kvTable)
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
		kv.mu.Lock()
		newConfig := kv.mck.Query(kv.config.Num + 1)
		shardsToWaitFor := make([]int, 0)
		if kv.isLeader && newConfig.Num == kv.config.Num+1 && !kv.isReconfiguring {
			DPrintf("%v-%vc%v: New shard config detected %v %v, old shard config: %v\n",
				kv.gid, kv.me, kv.config.Num, newConfig.Num, newConfig.Shards, kv.config.Shards)
			kv.isReconfiguring = true
			nextConfigKvTable := make(map[int]map[string]string)
			for shard, newOwner := range newConfig.Shards {
				prevOwner := kv.config.Shards[shard]
				DPrintf("%v-%vc%v: Processing new config shard %v, prev owner %v, new owner %v",
					kv.gid, kv.me, kv.config.Num, shard, prevOwner, newOwner)
				if kv.gid == newOwner && prevOwner == 0 {
					// First owner
					nextConfigKvTable[shard] = make(map[string]string)
				} else if (kv.gid == prevOwner || prevOwner == -1) && kv.gid != newOwner {
					// Lose shard
					// We need to make sure all servers have released a shard at this point to
					// guard against the following scenario.
					// - Leader gave shard.
					// - Recipient started serving new shard.
					// - Leader then disconnects; another server in the same group starts serving
					//   the same shard.
					kv.submitAndWait(Op{RequestId: nrand(), Name: "ReleaseShard", Shard: shard})
					DPrintf("%v-%vc%v: Release shard %v done\n", kv.gid, kv.me, kv.config.Num, shard)
					// Give shard
					kv.giveShard(newConfig, newOwner, shard)
					DPrintf("%v-%vc%v: Give shard %v done\n", kv.gid, kv.me, kv.config.Num, shard)
				} else if kv.gid == newOwner && kv.gid != prevOwner {
					// Gain shard
					DPrintf("%v-%vc%v: Add shard %v to wait from %v\n",
						kv.gid, kv.me, kv.config.Num, shard, prevOwner)
					shardsToWaitFor = append(shardsToWaitFor, shard)
				}
			}
			for _, shard := range shardsToWaitFor {
				for {
					DPrintf("%v-%vc%v: Wait for shard %v for config %v\n", kv.gid, kv.me, kv.config.Num, shard, newConfig.Num)
					if _, ok := kv.savedShardKvTable[newConfig.Num][shard]; ok {
						DPrintf("%v-%vc%v: Received shard %v\n", kv.gid, kv.me, kv.config.Num, shard)
						nextConfigKvTable[shard] = kv.savedShardKvTable[newConfig.Num][shard]
						break
					}
					kv.mu.Unlock()
					// Wait until shard received
					time.Sleep(7 * HouseKeepersSleepTime)
					if kv.killed() {
						return
					}
					kv.mu.Lock()
					if !kv.isLeader || !kv.isReconfiguring || newConfig.Num != kv.config.Num+1 {
						DPrintf("%v-%vc%v: Config %v applied while waiting for shard %v, config %v, stop waiting\n",
							kv.gid, kv.me, kv.config.Num, kv.config.Num, shard, newConfig.Num)
						kv.mu.Unlock()
						return
					}
				}
			}
			kv.submitAndWait(Op{RequestId: nrand(), Name: "ApplyNewConfig", Config: newConfig,
				KvTable: nextConfigKvTable})
		}
		kv.mu.Unlock()
		time.Sleep(HouseKeepersSleepTime)
	}
}

func (kv *ShardKV) giveShard(newConfig shardmaster.Config, gid int, shard int) {
	DPrintf("%v-%vc%v: Give shard %v to gid %v\n", kv.gid, kv.me, kv.config.Num, shard, gid)
	args := GiveShardArgs{RequestId: nrand(), Config: newConfig, Shard: shard, ShardKvTable: kv.kvTable[shard]}
	servers := newConfig.Groups[gid]
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
		_, isLeader := kv.rf.GetState()
		kv.mu.Lock()
		if !kv.isLeader && isLeader {
			DPrintf("%v-%vc%v: Gained leadership\n", kv.gid, kv.me, kv.config.Num)
			kv.isLeader = true
		} else if kv.isLeader && !isLeader {
			DPrintf("%v-%vc%v: Lost leadership\n", kv.gid, kv.me, kv.config.Num)
			kv.closeOpenRequests()
			kv.isLeader = false
		}
		kv.mu.Unlock()
		time.Sleep(HouseKeepersSleepTime / 10)
	}
}

func (kv *ShardKV) closeOpenRequests() {
	for requestId := range kv.openRequests {
		kv.signalRequestCh(requestId, false)
	}
}

func (kv *ShardKV) signalRequestCh(requestId int64, value bool) {
	DPrintf("%v-%vc%v:%v: Signal %v to open request channel\n", kv.gid, kv.me, kv.config.Num, requestId, value)
	kv.openRequests[requestId] <- value
	delete(kv.openRequests, requestId)
}

func (kv *ShardKV) maxRaftStateHandler() {
	for {
		if kv.killed() {
			return
		}
		if kv.rf.GetStateSize() >= kv.maxraftstate {
			kv.mu.Lock()
			if kv.lastExecutedIndex != kv.snapshotIndex {
				DPrintf("%v-%vc%v: Begin to save snapshot at log index %d\n", kv.gid, kv.me, kv.config.Num, kv.lastExecutedIndex)
				w := new(bytes.Buffer)
				e := labgob.NewEncoder(w)
				e.Encode(kv.kvTable)
				e.Encode(kv.executedRequests)
				e.Encode(kv.lastExecutedIndex)
				e.Encode(kv.config)
				e.Encode(kv.savedShardKvTable)
				e.Encode(kv.isReconfiguring)
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
	var savedShardKvTable map[int]map[int]map[string]string
	var isReconfiguring bool
	if d.Decode(&kvTable) != nil || d.Decode(&executedRequests) != nil || d.Decode(&lastExecutedIndex) != nil ||
		d.Decode(&config) != nil || d.Decode(&savedShardKvTable) != nil || d.Decode(&isReconfiguring) != nil {
		DPrintf("%d: Failed to restore snapshot\n", kv.me)
		return
	} else {
		kv.kvTable = kvTable
		kv.executedRequests = executedRequests
		kv.lastExecutedIndex = lastExecutedIndex
		kv.config = config
		kv.savedShardKvTable = savedShardKvTable
		kv.isReconfiguring = isReconfiguring
	}
}

func (kv *ShardKV) submitAndWait(op Op) bool {
	kv.openRequests[op.RequestId] = make(chan bool)
	requestCh := kv.openRequests[op.RequestId]
	kv.mu.Unlock()
	index, term, _ := kv.rf.Start(op)
	DPrintf("%v-%vc%v: Submitted op %v for index %v term %v\n", kv.gid, kv.me, kv.config.Num, op.Name, index, term)
	ok := <-requestCh
	kv.mu.Lock()
	if !ok {
		DPrintf("%v-%vc%v:%v: Op %v failed\n", kv.gid, kv.me, kv.config.Num, op.RequestId, op.Name)
		return false
	}
	return true
}

func copyKvTable(src map[string]string) map[string]string {
	dst := make(map[string]string)
	for k, v := range src {
		dst[k] = v
	}
	return dst
}
