package kvraft

import (
	"bytes"
	"log"
	"sync"
	"sync/atomic"
	"time"

	"../labgob"
	"../labrpc"
	"../raft"
)

const Debug = 1
const HousekeepersSleepTime = 10 * time.Millisecond

func DPrintf(format string, a ...interface{}) (n int, err error) {
	if Debug > 0 {
		log.Printf(format, a...)
	}
	return
}

type Op struct {
	LastExecutedRequestId int64
	RequestId             int64
	Name                  string
	Key                   string
	Value                 string
}

type KVServer struct {
	mu      sync.Mutex
	me      int
	rf      *raft.Raft
	applyCh chan raft.ApplyMsg
	dead    int32 // set by Kill()

	maxraftstate int // snapshot if log grows this big

	table             map[string]string
	currentTerm       int
	isLeader          bool
	requestWaitCh     map[int64]chan bool
	indexRequest      map[int]int64
	termRequests      map[int][]int64
	requestExecuted   map[int64]bool
	lastExecutedIndex int
	snapshotIndex     int
}

func (kv *KVServer) Get(args *GetArgs, reply *GetReply) {
	DPrintf("%d: Received Get request %v\n", kv.me, args)
	reply.Err = OK
	index, term, isLeader := kv.rf.Start(Op{LastExecutedRequestId: args.LastRequestId,
		RequestId: args.RequestId, Name: "Get", Key: args.Key})
	DPrintf("%d:%v: Submitted for index %d term %d\n", kv.me, args.RequestId, index, term)
	kv.mu.Lock()
	if term != kv.currentTerm {
		kv.handleTermChange(term)
	}
	if !isLeader {
		DPrintf("%d:%v: Not leader\n", kv.me, args.RequestId)
		reply.Err = ErrWrongLeader
		kv.mu.Unlock()
		return
	}
	kv.isLeader = true
	kv.requestWaitCh[args.RequestId] = make(chan bool)
	kv.indexRequest[index] = args.RequestId
	if requests, ok := kv.termRequests[term]; ok {
		kv.termRequests[term] = append(requests, args.RequestId)
	} else {
		kv.termRequests[term] = make([]int64, 0)
		kv.termRequests[term] = append(kv.termRequests[term], args.RequestId)
	}
	waitCh := kv.requestWaitCh[args.RequestId]
	kv.mu.Unlock()
	ok := <-waitCh
	kv.mu.Lock()
	if !ok {
		DPrintf("%d:%v: Not committed\n", kv.me, args.RequestId)
		reply.Err = ErrNotCommitted
		kv.mu.Unlock()
		return
	}
	reply.Value = kv.table[args.Key]
	DPrintf("%d:%v: OK\n", kv.me, args.RequestId)
	kv.mu.Unlock()
}

func (kv *KVServer) PutAppend(args *PutAppendArgs, reply *PutAppendReply) {
	DPrintf("%d: Received PutAppend request %v\n", kv.me, args)
	reply.Err = OK
	index, term, isLeader := kv.rf.Start(Op{
		LastExecutedRequestId: args.LastRequestId,
		RequestId:             args.RequestId, Name: args.Op,
		Key: args.Key, Value: args.Value,
	})
	DPrintf("%d:%v: Submitted for index %d term %d\n", kv.me, args.RequestId, index, term)
	kv.mu.Lock()
	if term != kv.currentTerm {
		kv.handleTermChange(term)
	}
	if !isLeader {
		DPrintf("%d:%v: Not leader\n", kv.me, args.RequestId)
		reply.Err = ErrWrongLeader
		kv.mu.Unlock()
		return
	}
	kv.isLeader = true
	kv.requestWaitCh[args.RequestId] = make(chan bool)
	kv.indexRequest[index] = args.RequestId
	if _, ok := kv.termRequests[term]; ok {
		kv.termRequests[term] = append(kv.termRequests[term], args.RequestId)
	} else {
		kv.termRequests[term] = make([]int64, 0)
		kv.termRequests[term] = append(kv.termRequests[term], args.RequestId)
	}
	waitCh := kv.requestWaitCh[args.RequestId]
	kv.mu.Unlock()
	ok := <-waitCh
	kv.mu.Lock()
	if !ok {
		DPrintf("%d:%v: Not committed\n", kv.me, args.RequestId)
		reply.Err = ErrNotCommitted
		kv.mu.Unlock()
		return
	}
	kv.mu.Unlock()
	DPrintf("%d:%v: OK\n", kv.me, args.RequestId)
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
	kv.currentTerm = 0
	kv.requestWaitCh = make(map[int64]chan bool)
	kv.indexRequest = make(map[int]int64)
	kv.termRequests = make(map[int][]int64)
	kv.requestExecuted = make(map[int64]bool)
	kv.lastExecutedIndex = 0
	kv.snapshotIndex = 0

	kv.restoreSnapshot(kv.rf.GetSavedSnapshot())

	go kv.applyMsgHandler()
	go kv.termChangeHandler()
	if maxraftstate != -1 {
		go kv.maxRaftStateHandler()
	}

	return kv
}

func (kv *KVServer) restoreSnapshot(data []byte) {
	if data == nil || len(data) < 1 {
		return
	}
	r := bytes.NewBuffer(data)
	d := labgob.NewDecoder(r)
	var table map[string]string
	var requestExecuted map[int64]bool
	var lastExecutedIndex int
	if d.Decode(&table) != nil || d.Decode(&requestExecuted) != nil || d.Decode(&lastExecutedIndex) != nil {
		DPrintf("%d: Failed to restore snapshot\n", kv.me)
		return
	} else {
		kv.table = table
		kv.requestExecuted = requestExecuted
		kv.lastExecutedIndex = lastExecutedIndex
	}
}

func (kv *KVServer) applyMsgHandler() {
	for {
		if kv.killed() {
			return
		}
		DPrintf("%d: Wait for applyMsg\n", kv.me)
		applyMsg := <-kv.applyCh
		kv.mu.Lock()
		if applyMsg.CommandValid {
			cmd := applyMsg.Command.(Op)
			DPrintf("%d:%v: Received applyMsg %v\n", kv.me, cmd.RequestId, applyMsg)
			if cmd.LastExecutedRequestId != 0 {
				delete(kv.requestExecuted, cmd.LastExecutedRequestId)
			}
			kv.executeCommand(cmd, applyMsg.CommandIndex)
			if _, ok := kv.requestWaitCh[cmd.RequestId]; ok {
				kv.signalWaitCh(cmd.RequestId, true)
			} else if requestId, ok := kv.indexRequest[applyMsg.CommandIndex]; ok && requestId != cmd.RequestId {
				DPrintf("%d:%v: Different request id at index\n", kv.me, cmd.RequestId)
				if _, ok := kv.requestWaitCh[requestId]; ok {
					kv.signalWaitCh(requestId, false)
				}
				delete(kv.indexRequest, applyMsg.CommandIndex)
			}
		} else if applyMsg.IsSnapshot {
			kv.restoreSnapshot(applyMsg.Data)
			kv.lastExecutedIndex = applyMsg.CommandIndex
		}
		kv.mu.Unlock()
	}
}

func (kv *KVServer) termChangeHandler() {
	for {
		if kv.killed() {
			return
		}
		term, isLeader := kv.rf.GetState()
		kv.mu.Lock()
		if term != kv.currentTerm {
			kv.handleTermChange(term)
		}
		if kv.isLeader && !isLeader {
			DPrintf("%d: Detected lost leadership\n", kv.me)
			kv.isLeader = false
			for requestId, _ := range kv.requestWaitCh {
				kv.signalWaitCh(requestId, false)
			}
		}
		kv.mu.Unlock()
		time.Sleep(HousekeepersSleepTime)
	}
}

func (kv *KVServer) executeCommand(cmd Op, cmdIdx int) {
	executed := kv.requestExecuted[cmd.RequestId]
	if !executed {
		DPrintf("%d:%v: Execute command\n", kv.me, cmd.RequestId)
		if cmd.Name == "Put" {
			kv.table[cmd.Key] = cmd.Value
		} else if cmd.Name == "Append" {
			kv.table[cmd.Key] = kv.table[cmd.Key] + cmd.Value
		}
		kv.requestExecuted[cmd.RequestId] = true
		kv.lastExecutedIndex = cmdIdx
	} else {
		DPrintf("%d:%v: Duplicate command detected\n", kv.me, cmd.RequestId)
	}
}

func (kv *KVServer) signalWaitCh(requestId int64, result bool) {
	DPrintf("%d:%v: Signal %v to waitCh\n", kv.me, requestId, result)
	kv.requestWaitCh[requestId] <- result
	delete(kv.requestWaitCh, requestId)
	DPrintf("%d:%v: Done signaling\n", kv.me, requestId)
}

func (kv *KVServer) handleTermChange(newTerm int) {
	DPrintf("%d: Detected term change from %d to %d\n", kv.me, kv.currentTerm, newTerm)
	for _, requestId := range kv.termRequests[kv.currentTerm] {
		if _, ok := kv.requestWaitCh[requestId]; ok {
			kv.signalWaitCh(requestId, false)
		}
	}
	delete(kv.termRequests, kv.currentTerm)
	kv.currentTerm = newTerm
}

func (kv *KVServer) maxRaftStateHandler() {
	for {
		if kv.killed() {
			return
		}
		if kv.rf.GetStateSize() >= kv.maxraftstate {
			kv.mu.Lock()
			if kv.lastExecutedIndex != kv.snapshotIndex {
				DPrintf("%d: Begin to save snapshot at log index %d\n", kv.me, kv.lastExecutedIndex)
				w := new(bytes.Buffer)
				e := labgob.NewEncoder(w)
				e.Encode(kv.table)
				e.Encode(kv.requestExecuted)
				e.Encode(kv.lastExecutedIndex)
				data := w.Bytes()
				lastExecutedIndex := kv.lastExecutedIndex
				kv.snapshotIndex = kv.lastExecutedIndex
				kv.mu.Unlock()
				kv.rf.SaveSnapshot(data, lastExecutedIndex)
			} else {
				kv.mu.Unlock()
			}
		}
		time.Sleep(HousekeepersSleepTime)
	}
}
