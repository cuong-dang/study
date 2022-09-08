package shardmaster

import (
	"../raft"
	"log"
	"sort"
	"time"
)
import "../labrpc"
import "sync"
import "../labgob"

// Debug toggles debug messages.
const Debug = 0

// DPrintf prints if const Debug is not 0.
func DPrintf(format string, a ...interface{}) (n int, err error) {
	if Debug > 0 {
		log.Printf(format, a...)
	}
	return
}

// ShardMaster contains data structures for a Shard Master server.
type ShardMaster struct {
	mu      sync.Mutex
	me      int
	rf      *raft.Raft
	applyCh chan raft.ApplyMsg

	isKilled         bool
	isLeader         bool
	openRequests     map[int64]chan bool
	executedRequests map[int64]bool

	configs []Config // indexed by config num
}

// HouseKeepersSleepTime specifies sleep time of housekeeping routines.
const HouseKeepersSleepTime = 10 * time.Millisecond

// OpName is operation type.
type OpName int

// Operation types
const (
	Query OpName = iota
	Join
	Leave
	Move
)

// OpCommon contains operations' common data.
type OpCommon struct {
	Name      OpName
	RequestId int64
}

// Op contains data for all operations and dispatches on OpName.
type Op struct {
	OpCommon
	QArgs QueryArgs
	JArgs JoinArgs
	LArgs LeaveArgs
	MArgs MoveArgs
}

// Join receives a Join request.
func (sm *ShardMaster) Join(args *JoinArgs, reply *JoinReply) {
	DPrintf("%v: Received Join request\n", sm.me)
	reply.WrongLeader = false
	index, term, isLeader := sm.rf.Start(Op{
		OpCommon: OpCommon{Name: Join, RequestId: args.RequestId},
		JArgs:    JoinArgs{RequestCommon: RequestCommon{RequestId: args.RequestId}, Servers: copyMap(args.Servers)},
	})
	DPrintf("%v:%v: Submitted for index %d term %d\n", sm.me, args.RequestId, index, term)
	sm.mu.Lock()
	if !isLeader {
		DPrintf("%d:%v: Not leader\n", sm.me, args.RequestId)
		reply.WrongLeader = true
		sm.mu.Unlock()
		return
	}
	sm.isLeader = true
	sm.openRequests[args.RequestId] = make(chan bool)
	requestCh := sm.openRequests[args.RequestId]
	sm.mu.Unlock()
	ok := <-requestCh
	sm.mu.Lock()
	if !ok {
		DPrintf("%d:%v: Failed to process request\n", sm.me, args.RequestId)
		reply.WrongLeader = true
		sm.mu.Unlock()
		return
	}
	DPrintf("%d:%v: OK\n", sm.me, args.RequestId)
	sm.mu.Unlock()
	return
}

func (sm *ShardMaster) Leave(args *LeaveArgs, reply *LeaveReply) {
	DPrintf("%v: Received Leave request\n", sm.me)
	reply.WrongLeader = false
	gids := make([]int, len(args.GIDs))
	copy(gids, args.GIDs)
	index, term, isLeader := sm.rf.Start(Op{
		OpCommon: OpCommon{Name: Leave, RequestId: args.RequestId},
		LArgs:    LeaveArgs{RequestCommon: RequestCommon{RequestId: args.RequestId}, GIDs: gids},
	})
	DPrintf("%v:%v: Submitted for index %d term %d\n", sm.me, args.RequestId, index, term)
	sm.mu.Lock()
	if !isLeader {
		DPrintf("%d:%v: Not leader\n", sm.me, args.RequestId)
		reply.WrongLeader = true
		sm.mu.Unlock()
		return
	}
	sm.isLeader = true
	sm.openRequests[args.RequestId] = make(chan bool)
	requestCh := sm.openRequests[args.RequestId]
	sm.mu.Unlock()
	ok := <-requestCh
	sm.mu.Lock()
	if !ok {
		DPrintf("%d:%v: Failed to process request\n", sm.me, args.RequestId)
		reply.WrongLeader = true
		sm.mu.Unlock()
		return
	}
	DPrintf("%d:%v: OK\n", sm.me, args.RequestId)
	sm.mu.Unlock()
	return
}

func (sm *ShardMaster) Move(args *MoveArgs, reply *MoveReply) {
	DPrintf("%v: Received Move request\n", sm.me)
	reply.WrongLeader = false
	index, term, isLeader := sm.rf.Start(Op{
		OpCommon: OpCommon{Name: Move, RequestId: args.RequestId},
		MArgs:    MoveArgs{RequestCommon: RequestCommon{RequestId: args.RequestId}, Shard: args.Shard, GID: args.GID},
	})
	DPrintf("%v:%v: Submitted for index %d term %d\n", sm.me, args.RequestId, index, term)
	sm.mu.Lock()
	if !isLeader {
		DPrintf("%d:%v: Not leader\n", sm.me, args.RequestId)
		reply.WrongLeader = true
		sm.mu.Unlock()
		return
	}
	sm.isLeader = true
	sm.openRequests[args.RequestId] = make(chan bool)
	requestCh := sm.openRequests[args.RequestId]
	sm.mu.Unlock()
	ok := <-requestCh
	sm.mu.Lock()
	if !ok {
		DPrintf("%d:%v: Failed to process request\n", sm.me, args.RequestId)
		reply.WrongLeader = true
		sm.mu.Unlock()
		return
	}
	DPrintf("%d:%v: OK\n", sm.me, args.RequestId)
	sm.mu.Unlock()
	return
}

func (sm *ShardMaster) Query(args *QueryArgs, reply *QueryReply) {
	DPrintf("%v: Received Query request\n", sm.me)
	reply.WrongLeader = false
	index, term, isLeader := sm.rf.Start(Op{
		OpCommon: OpCommon{Name: Query, RequestId: args.RequestId},
		QArgs:    QueryArgs{RequestCommon: RequestCommon{RequestId: args.RequestId}, Num: args.Num},
	})
	DPrintf("%v:%v: Submitted for index %d term %d\n", sm.me, args.RequestId, index, term)
	sm.mu.Lock()
	if !isLeader {
		DPrintf("%d:%v: Not leader\n", sm.me, args.RequestId)
		reply.WrongLeader = true
		sm.mu.Unlock()
		return
	}
	sm.isLeader = true
	sm.openRequests[args.RequestId] = make(chan bool)
	requestCh := sm.openRequests[args.RequestId]
	sm.mu.Unlock()
	ok := <-requestCh
	sm.mu.Lock()
	if !ok {
		DPrintf("%d:%v: Failed to process request\n", sm.me, args.RequestId)
		reply.WrongLeader = true
		sm.mu.Unlock()
		return
	}
	if args.Num == -1 || args.Num > len(sm.configs) {
		reply.Config = sm.configs[len(sm.configs)-1]
	} else {
		reply.Config = sm.configs[args.Num]
	}
	DPrintf("%d:%v: OK\n", sm.me, args.RequestId)
	sm.mu.Unlock()
	return
}

//
// the tester calls Kill() when a ShardMaster instance won't
// be needed again. you are not required to do anything
// in Kill(), but it might be convenient to (for example)
// turn off debug output from this instance.
//
func (sm *ShardMaster) Kill() {
	sm.rf.Kill()
	sm.isKilled = true
}

// needed by shardkv tester
func (sm *ShardMaster) Raft() *raft.Raft {
	return sm.rf
}

//
// servers[] contains the ports of the set of
// servers that will cooperate via Paxos to
// form the fault-tolerant shardmaster service.
// me is the index of the current server in servers[].
//
func StartServer(servers []*labrpc.ClientEnd, me int, persister *raft.Persister) *ShardMaster {
	sm := new(ShardMaster)
	sm.me = me

	sm.configs = make([]Config, 1)
	sm.configs[0].Groups = map[int][]string{}

	labgob.Register(Op{})
	sm.applyCh = make(chan raft.ApplyMsg)
	sm.rf = raft.Make(servers, me, persister, sm.applyCh)

	sm.openRequests = make(map[int64]chan bool)
	sm.executedRequests = make(map[int64]bool)
	go sm.applyChProcessor()
	go sm.leaderShipChangePoller()

	DPrintf("%v: Shard Master server started\n", sm.me)
	return sm
}

// applyChProcessor waits on chan applyCh to receive messages and process
// them.
func (sm *ShardMaster) applyChProcessor() {
	for {
		if sm.isKilled {
			return
		}
		msg := <-sm.applyCh
		if msg.CommandValid {
			op := msg.Command.(Op)
			DPrintf("%d:%v: Received %v applyMsg %v\n", sm.me, op.RequestId, op.Name, msg)
			sm.mu.Lock()
			if sm.executedRequests[op.RequestId] {
				DPrintf("%d:%v: Request already executed\n", sm.me, op.RequestId)
				sm.mu.Unlock()
				return
			}
			switch op.Name {
			case Join:
				sm.executeJoinRequest(op)
			case Leave:
				sm.executeLeaveRequest(op)
			case Move:
				sm.executeMoveRequest(op)
			}
			sm.executedRequests[op.RequestId] = true
			if _, ok := sm.openRequests[op.RequestId]; ok {
				sm.signalRequestCh(op.RequestId, true)
			}
			sm.mu.Unlock()
		}
	}
}

// executeJoinRequest executes Join requests.
func (sm *ShardMaster) executeJoinRequest(op Op) {
	DPrintf("%d: New servers joining %v\n", sm.me, op.JArgs.Servers)
	newConfig := createNewConfig(sm.configs[len(sm.configs)-1])
	serverGroups := op.JArgs.Servers
	// Insert new server groups into new config.
	for gid, servers := range serverGroups {
		// Assuming all new gid
		newConfig.Groups[gid] = make([]string, 0, len(servers))
		newConfig.Groups[gid] = append(newConfig.Groups[gid], servers...)
	}
	// Re-balance shards
	ReBalance(newConfig.Shards[:], newConfig.Groups)
	sm.configs = append(sm.configs, newConfig)
	DPrintf("%d: New mapping after Join %v\n", sm.me, sm.configs[len(sm.configs)-1].Shards)
}

// executeLeaveRequest executes Leave requests.
func (sm *ShardMaster) executeLeaveRequest(op Op) {
	DPrintf("%d: Servers leaving %v\n", sm.me, op.LArgs.GIDs)
	newConfig := createNewConfig(sm.configs[len(sm.configs)-1])
	for _, leavingServer := range op.LArgs.GIDs {
		delete(newConfig.Groups, leavingServer)
	}
	ReBalance(newConfig.Shards[:], newConfig.Groups)
	sm.configs = append(sm.configs, newConfig)
	DPrintf("%d: New mapping after Leave %v\n", sm.me, newConfig.Shards)
}

// executeMoveRequest executes Move requests.
func (sm *ShardMaster) executeMoveRequest(op Op) {
	DPrintf("%d: Move shard %d to server %d\n", sm.me, op.MArgs.Shard, op.MArgs.GID)
	newConfig := createNewConfig(sm.configs[len(sm.configs)-1])
	newConfig.Shards[op.MArgs.Shard] = op.MArgs.GID
	sm.configs = append(sm.configs, newConfig)
	DPrintf("%d: New mapping after Leave %v\n", sm.me, newConfig.Shards)
}

// createNewConfig creates a new config as a copy of config with field Num
// increased by 1.
func createNewConfig(config Config) Config {
	newConfig := Config{Num: config.Num + 1, Groups: make(map[int][]string)}
	for shard, gid := range config.Shards {
		newConfig.Shards[shard] = gid
	}
	for gid, servers := range config.Groups {
		newConfig.Groups[gid] = make([]string, 0, len(servers))
		newConfig.Groups[gid] = append(newConfig.Groups[gid], servers...)
	}
	return newConfig
}

// ReBalance re-balances current shard distribution given new server groups.
func ReBalance(currMappingSlice []int, serverGroups map[int][]string) {
	// No servers available
	if len(serverGroups) == 0 {
		for shard := range currMappingSlice {
			currMappingSlice[shard] = 0
		}
		return
	}

	// Sort current mapping based on number of shards
	mapping := MakeMapping(currMappingSlice)
	sortedMapping := make([]int, len(mapping))
	i := 0
	for currServer := range mapping {
		sortedMapping[i] = currServer
		i += 1
	}
	sort.SliceStable(sortedMapping, func(i, j int) bool {
		if len(mapping[i]) > len(mapping[j]) {
			return true
		} else {
			return sortedMapping[i] < sortedMapping[j]
		}
	})
	// Prep vars
	numNewServers := len(serverGroups)
	numServersWithMoreShards := NShards % numNewServers
	lowerBoundNumShards := NShards / numNewServers
	remainingShardsToDistribute := make([]int, 0)
	newMapping := make(map[int][]int)
	// First pass
	// - Old servers that do not leave take maximum number of existing shards.
	// - Old servers that leave give up their shards for redistribution.
	for _, currServer := range sortedMapping {
		currShards := mapping[currServer]
		if _, ok := serverGroups[currServer]; ok {
			var numShards int
			if numServersWithMoreShards > 0 {
				numShards = lowerBoundNumShards + 1
				numServersWithMoreShards -= 1
			} else {
				numShards = lowerBoundNumShards
			}
			if numShards > len(currShards) {
				numShards = len(currShards)
			}
			if _, ok := newMapping[currServer]; !ok {
				newMapping[currServer] = make([]int, 0)
			}
			newMapping[currServer] = append(newMapping[currServer], currShards[:numShards]...)
			remainingShardsToDistribute = append(remainingShardsToDistribute, currShards[numShards:]...)
		} else {
			for _, shard := range currShards {
				remainingShardsToDistribute = append(remainingShardsToDistribute, shard)
			}
		}
	}
	// Second pass:
	// - Old servers that do not leave potentially take more shards.
	numServersWithMoreShards = NShards % numNewServers
	for _, currServer := range sortedMapping {
		if _, ok := serverGroups[currServer]; ok {
			var numShards int
			if numServersWithMoreShards > 0 {
				numShards = lowerBoundNumShards + 1
				numServersWithMoreShards -= 1
			} else {
				numShards = lowerBoundNumShards
			}
			if len(newMapping[currServer]) < numShards {
				numShardsToTake := numShards - len(newMapping[currServer])
				newMapping[currServer] = append(newMapping[currServer],
					remainingShardsToDistribute[:numShardsToTake]...)
				remainingShardsToDistribute = remainingShardsToDistribute[numShardsToTake:]
			}
		}
	}
	// Final pass:
	// - New servers take the rest of the shards.
	newServers := make([]int, 0)
	for newServer := range serverGroups {
		if _, ok := mapping[newServer]; !ok {
			newServers = append(newServers, newServer)
		}
	}
	sort.Ints(newServers)
	for _, newServer := range newServers {
		var numShards int
		if numServersWithMoreShards > 0 {
			numShards = lowerBoundNumShards + 1
			numServersWithMoreShards -= 1
		} else {
			numShards = lowerBoundNumShards
		}
		if _, ok := newMapping[newServer]; !ok {
			newMapping[newServer] = make([]int, 0)
		}
		newMapping[newServer] = append(newMapping[newServer], remainingShardsToDistribute[:numShards]...)
		remainingShardsToDistribute = remainingShardsToDistribute[numShards:]
	}
	// Set new mapping slice
	for server, shards := range newMapping {
		for _, shard := range shards {
			currMappingSlice[shard] = server
		}
	}
}

func MakeMapping(mappingSlice []int) map[int][]int {
	mapping := make(map[int][]int)
	for shard, server := range mappingSlice {
		if _, ok := mapping[server]; !ok {
			mapping[server] = make([]int, 0)
		}
		mapping[server] = append(mapping[server], shard)
	}
	return mapping
}

// leaderShipChangePoller periodically checks whether the current SM has
// lost leadership. If yes, the function closes all open requests.
func (sm *ShardMaster) leaderShipChangePoller() {
	for {
		if sm.isKilled {
			return
		}
		sm.mu.Lock()
		if sm.isLeader {
			_, stillLeader := sm.rf.GetState()
			if !stillLeader {
				DPrintf("%v: Lost leadership\n", sm.me)
				sm.closeOpenRequests()
			}
		}
		sm.mu.Unlock()
		time.Sleep(HouseKeepersSleepTime)
	}
}

// closeOpenRequests closes all open requests.
func (sm *ShardMaster) closeOpenRequests() {
	for requestId := range sm.openRequests {
		sm.signalRequestCh(requestId, false)
	}
}

// signalRequestCh sends a message to an open request's channel.
func (sm *ShardMaster) signalRequestCh(requestId int64, value bool) {
	DPrintf("%v:%v: Signal %v to open request channel\n", sm.me, requestId, value)
	sm.openRequests[requestId] <- value
	delete(sm.openRequests, requestId)
}

// copyMap returns a new map with src copied.
func copyMap(src map[int][]string) map[int][]string {
	dst := make(map[int][]string)
	for k, v := range src {
		dst[k] = make([]string, 0, len(v))
		dst[k] = append(dst[k], v...)
	}
	return dst
}
