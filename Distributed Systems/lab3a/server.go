package kvraft

import (
	"log"
	"sync"
	"sync/atomic"

	"../labgob"
	"../labrpc"
	"../raft"
)

const Debug = 0

func DPrintf(format string, a ...interface{}) (n int, err error) {
	if Debug > 0 {
		log.Printf(format, a...)
	}
	return
}

type Op struct {
	RequestId string
	Name      string
	Key       string
	Value     string
}

type KVServer struct {
	mu      sync.Mutex
	me      int
	rf      *raft.Raft
	applyCh chan raft.ApplyMsg
	dead    int32 // set by Kill()

	maxraftstate int // snapshot if log grows this big

	table         map[string]string
	requestWaitCh map[string]chan bool
}

func (kv *KVServer) Get(args *GetArgs, reply *GetReply) {
	kv.mu.Lock()
	reply.Err = OK

	_, _, isLeader := kv.rf.Start(Op{RequestId: args.RequestId, Name: "Get", Key: args.Key})
	if !isLeader {
		reply.Err = ErrWrongLeader
		kv.mu.Unlock()
		return
	}
	kv.requestWaitCh[args.RequestId] = make(chan bool)
	waitCh := kv.requestWaitCh[args.RequestId]
	kv.mu.Unlock()
	<-waitCh
	kv.mu.Lock()
	reply.Value = kv.table[args.Key]
	kv.mu.Unlock()
}

func (kv *KVServer) PutAppend(args *PutAppendArgs, reply *PutAppendReply) {
	kv.mu.Lock()
	reply.Err = OK

	_, _, isLeader := kv.rf.Start(Op{RequestId: args.RequestId, Name: args.Op,
		Key: args.Key, Value: args.Value})
	if !isLeader {
		reply.Err = ErrWrongLeader
		kv.mu.Unlock()
		return
	}
	kv.requestWaitCh[args.RequestId] = make(chan bool)
	waitCh := kv.requestWaitCh[args.RequestId]
	kv.mu.Unlock()
	<-waitCh
}

//
// the tester calls Kill() when a KVServer instance won't
// be needed again. for your convenience, we supply
// code to set rf.dead (without needing a lock),
// and a killed() method to test rf.dead in
// long-running loops. you can also add your own
// code to Kill(). you're not required to do anything
// about this, but it may be convenient (for example)
// to suppress debug output from a Kill()ed instance.
//
func (kv *KVServer) Kill() {
	atomic.StoreInt32(&kv.dead, 1)
	kv.rf.Kill()
	// Your code here, if desired.
}

func (kv *KVServer) killed() bool {
	z := atomic.LoadInt32(&kv.dead)
	return z == 1
}

//
// servers[] contains the ports of the set of
// servers that will cooperate via Raft to
// form the fault-tolerant key/value service.
// me is the index of the current server in servers[].
// the k/v server should store snapshots through the underlying Raft
// implementation, which should call persister.SaveStateAndSnapshot() to
// atomically save the Raft state along with the snapshot.
// the k/v server should snapshot when Raft's saved state exceeds maxraftstate bytes,
// in order to allow Raft to garbage-collect its log. if maxraftstate is -1,
// you don't need to snapshot.
// StartKVServer() must return quickly, so it should start goroutines
// for any long-running work.
//
func StartKVServer(servers []*labrpc.ClientEnd, me int, persister *raft.Persister, maxraftstate int) *KVServer {
	// call labgob.Register on structures you want
	// Go's RPC library to marshall/unmarshall.
	labgob.Register(Op{})

	kv := new(KVServer)
	kv.me = me
	kv.maxraftstate = maxraftstate

	kv.applyCh = make(chan raft.ApplyMsg)
	kv.rf = raft.Make(servers, me, persister, kv.applyCh)

	kv.table = make(map[string]string)
	kv.requestWaitCh = make(map[string]chan bool)

	go kv.applyMsgHandler()

	return kv
}

func (kv *KVServer) applyMsgHandler() {
	for {
		kv.mu.Lock()
		if kv.killed() {
			kv.mu.Unlock()
			return
		}
		kv.mu.Unlock()
		applyMsg := <-kv.applyCh
		kv.mu.Lock()
		if applyMsg.CommandValid {
			cmd := applyMsg.Command.(Op)
			DPrintf("%d: Received applyMsg %v\n", kv.me, cmd)
			if cmd.Name == "Put" {
				kv.table[cmd.Key] = cmd.Value
			} else if cmd.Name == "Append" {
				kv.table[cmd.Key] = kv.table[cmd.Key] + cmd.Value
			}
			if waitCh, ok := kv.requestWaitCh[cmd.RequestId]; ok {
				waitCh <- true
			}
		}
		kv.mu.Unlock()
	}
}
