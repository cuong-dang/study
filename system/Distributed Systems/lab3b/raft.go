package raft

//
// this is an outline of the API that raft must expose to
// the service (or tester). see comments below for
// each of these functions for more details.
//
// rf = Make(...)
//   create a new Raft server.
// rf.Start(command interface{}) (index, term, isleader)
//   start agreement on a new log entry
// rf.GetState() (term, isLeader)
//   ask a Raft for its current term, and whether it thinks it is leader
// ApplyMsg
//   each time a new entry is committed to the log, each Raft peer
//   should send an ApplyMsg to the service (or tester)
//   in the same server.
//

import (
	"bytes"
	"log"
	"math/rand"
	"sync"
	"sync/atomic"
	"time"

	"../labgob"
	"../labrpc"
)

// import "bytes"
// import "../labgob"

const PrintDebug = true

//
// as each Raft peer becomes aware that successive log entries are
// committed, the peer should send an ApplyMsg to the service (or
// tester) on the same server, via the applyCh passed to Make(). set
// CommandValid to true to indicate that the ApplyMsg contains a newly
// committed log entry.
//
// in Lab 3 you'll want to send other kinds of messages (e.g.,
// snapshots) on the applyCh; at that point you can add fields to
// ApplyMsg, but set CommandValid to false for these other uses.
//
type ApplyMsg struct {
	CommandValid bool
	Command      interface{}
	CommandIndex int
	IsSnapshot   bool
	Data         []byte
}

type LogEntry struct {
	Command interface{}
	Term    int
}

//
// A Go object implementing a single Raft peer.
//
type Raft struct {
	mu        sync.Mutex          // Lock to protect shared access to this peer's state
	peers     []*labrpc.ClientEnd // RPC end points of all peers
	persister *Persister          // Object to hold this peer's persisted state
	me        int                 // this peer's index into peers[]
	dead      int32               // set by Kill()

	// Your data here (2A, 2B, 2C).
	// Look at the paper's Figure 2 for a description of what
	// state a Raft server must maintain.
	state         RaftState
	currentTerm   int
	votedFor      int
	voteCount     int
	electionTimer int
	timeSince     int

	log               []LogEntry
	commitIndex       int
	lastApplied       int
	lastSnapshotIndex int
	lastSnapshotTerm  int

	nextIndex  []int
	matchIndex []int

	applyCh chan ApplyMsg
}

type RaftState int

const (
	follower RaftState = iota
	candidate
	leader
)

// return currentTerm and whether this server
// believes it is the leader.
func (rf *Raft) GetState() (int, bool) {

	var term int
	var isleader bool

	rf.mu.Lock()
	isleader = rf.state == leader
	term = rf.currentTerm
	rf.mu.Unlock()
	return term, isleader
}

func (rf *Raft) getState() []byte {
	w := new(bytes.Buffer)
	e := labgob.NewEncoder(w)
	e.Encode(rf.currentTerm)
	e.Encode(rf.votedFor)
	e.Encode(rf.log)
	e.Encode(rf.lastSnapshotIndex)
	e.Encode(rf.lastSnapshotTerm)
	return w.Bytes()
}

//
// save Raft's persistent state to stable storage,
// where it can later be retrieved after a crash and restart.
// see paper's Figure 2 for a description of what should be persistent.
//
func (rf *Raft) persist() {
	rf.persister.SaveRaftState(rf.getState())
}

//
// restore previously persisted state.
//
func (rf *Raft) readPersist(data []byte) {
	if data == nil || len(data) < 1 { // bootstrap without any state?
		return
	}
	r := bytes.NewBuffer(data)
	d := labgob.NewDecoder(r)
	var currentTerm int
	var votedFor int
	var log_ []LogEntry
	var lastSnapshotIndex int
	var lastSnapshotTerm int
	if d.Decode(&currentTerm) != nil || d.Decode(&votedFor) != nil || d.Decode(&log_) != nil ||
		d.Decode(&lastSnapshotIndex) != nil || d.Decode(&lastSnapshotTerm) != nil {
		log.Fatalf("%d: Failed to restore persisted state\n", rf.me)
		return
	} else {
		rf.currentTerm = currentTerm
		rf.votedFor = votedFor
		rf.log = log_
		rf.commitIndex = lastSnapshotIndex
		rf.lastSnapshotIndex = lastSnapshotIndex
		rf.lastSnapshotTerm = lastSnapshotTerm
	}
}

//
// example RequestVote RPC arguments structure.
// field names must start with capital letters!
//
type RequestVoteArgs struct {
	Term         int
	CandidateId  int
	LastLogIndex int
	LastLogTerm  int
}

//
// example RequestVote RPC reply structure.
// field names must start with capital letters!
//
type RequestVoteReply struct {
	Term        int
	VoteGranted bool
}

type AppendEntriesArgs struct {
	Term         int
	LeaderId     int
	PrevLogIndex int
	PrevLogTerm  int
	Entries      []LogEntry
	LeaderCommit int
}

type AppendEntriesReply struct {
	Term          int
	Success       bool
	ConflictIndex int
	ConflictTerm  int
}

type InstallSnapshotArgs struct {
	Term              int
	LeaderId          int
	LastIncludedIndex int
	LastIncludedTerm  int
	Data              []byte
}

type InstallSnapshotReply struct {
	Term int
}

//
// example RequestVote RPC handler.
//
func (rf *Raft) RequestVote(args *RequestVoteArgs, reply *RequestVoteReply) {
	rf.mu.Lock()
	if PrintDebug {
		log.Printf("%d: RequestVote RPC recevied from %d, term %d\n",
			rf.me, args.CandidateId, args.Term)
	}
	if args.Term < rf.currentTerm {
		if PrintDebug {
			log.Printf("%d: Vote declined to %d\n", rf.me, args.CandidateId)
		}
		reply.VoteGranted = false
		reply.Term = rf.currentTerm
		rf.persist()
		rf.mu.Unlock()
		return
	}
	if args.Term > rf.currentTerm {
		rf.currentTerm = args.Term
		rf.becomeFollower()
	}

	lastLogIndex, lastLogTerm := rf.getPhysicalLastLogIndexAndTerm()
	if (rf.votedFor == -1 || rf.votedFor == args.CandidateId) &&
		(lastLogTerm < args.LastLogTerm ||
			(lastLogTerm == args.LastLogTerm && lastLogIndex <= args.LastLogIndex)) {
		if PrintDebug {
			log.Printf("%d: Vote granted to %d\n", rf.me, args.CandidateId)
		}
		rf.votedFor = args.CandidateId
		reply.VoteGranted = true
		rf.resetElectionTimer()
	} else {
		reply.VoteGranted = false
	}
	reply.Term = rf.currentTerm
	rf.persist()
	rf.mu.Unlock()
}

func (rf *Raft) AppendEntries(args *AppendEntriesArgs, reply *AppendEntriesReply) {
	rf.mu.Lock()
	if PrintDebug {
		log.Printf("%d: AppendEntries RPC recevied, prev index %d, term %d, entries len %d\n",
			rf.me, args.PrevLogIndex, args.Term, len(args.Entries))
	}
	if args.Term < rf.currentTerm {
		reply.Success = false
		reply.Term = rf.currentTerm
		rf.persist()
		rf.mu.Unlock()
		return
	}
	if args.Term > rf.currentTerm {
		rf.currentTerm = args.Term
		rf.becomeFollower()
	}
	if rf.state == candidate {
		rf.becomeFollower()
	}

	reply.Term = rf.currentTerm
	var prevLogIndexTerm int
	if len(rf.log)+rf.lastSnapshotIndex > args.PrevLogIndex {
		if args.PrevLogIndex == rf.lastSnapshotIndex {
			prevLogIndexTerm = rf.lastSnapshotTerm
		} else if args.PrevLogIndex-rf.lastSnapshotIndex > 0 {
			prevLogIndexTerm = rf.log[args.PrevLogIndex-rf.lastSnapshotIndex].Term
		} else {
			// WARNING: THE FOLLOWING CODE'S REASONING IS NOT JUSTIFIED.
			// This case arises when append entries contain both old entries, whose index is smaller than the current
			// last snapshot index, and new entries.
			prevLogIndexTerm = args.PrevLogTerm
		}
	}
	if len(rf.log)+rf.lastSnapshotIndex <= args.PrevLogIndex || prevLogIndexTerm != args.PrevLogTerm {
		if PrintDebug {
			log.Printf("%d: Reject append entries %v %v\n",
				rf.me, len(rf.log)+rf.lastSnapshotIndex <= args.PrevLogIndex,
				args.PrevLogIndex == rf.lastSnapshotIndex && rf.lastSnapshotTerm != args.PrevLogTerm)
		}
		reply.Success = false
		if len(rf.log)+rf.lastSnapshotIndex <= args.PrevLogIndex {
			reply.ConflictIndex = len(rf.log) + rf.lastSnapshotIndex
			reply.ConflictTerm = -1
		} else {
			if args.PrevLogIndex == rf.lastSnapshotIndex {
				reply.ConflictTerm = rf.lastSnapshotTerm
				reply.ConflictIndex = rf.lastSnapshotIndex
			} else {
				reply.ConflictTerm = rf.log[args.PrevLogIndex-rf.lastSnapshotIndex].Term
				for i, entry := range rf.log {
					if entry.Term == reply.ConflictTerm {
						reply.ConflictIndex = i + rf.lastSnapshotIndex
						break
					}
				}
			}
		}
		if PrintDebug {
			log.Printf("%d: Conflict index: %d, conflict term: %d\n",
				rf.me, reply.ConflictIndex, reply.ConflictTerm)
		}
	} else {
		reply.Success = true
		logIndex := args.PrevLogIndex + 1
		for _, entry := range args.Entries {
			physicalLogIndex := logIndex - rf.lastSnapshotIndex
			if physicalLogIndex > 0 && physicalLogIndex < len(rf.log) {
				if rf.log[physicalLogIndex].Term != entry.Term {
					rf.log = rf.log[:physicalLogIndex]
					rf.log = append(rf.log, entry)
				}
			} else if physicalLogIndex > 0 {
				rf.log = append(rf.log, entry)
			}
			logIndex++
		}
		if args.LeaderCommit > rf.commitIndex {
			oldCommitIndex := rf.commitIndex
			lastNewEntryIndex := args.PrevLogIndex + len(args.Entries)
			if args.LeaderCommit < lastNewEntryIndex {
				rf.commitIndex = args.LeaderCommit
			} else {
				rf.commitIndex = lastNewEntryIndex
			}
			if rf.commitIndex > oldCommitIndex {
				rf.commitFrom(oldCommitIndex + 1)
			}
		}
	}
	rf.resetElectionTimer()
	rf.persist()
	rf.mu.Unlock()
}

func (rf *Raft) InstallSnapshot(args *InstallSnapshotArgs, reply *InstallSnapshotReply) {
	rf.mu.Lock()
	if PrintDebug {
		log.Printf("%d: InstallSnapshot RPC recevied, term %d, last included index %d\n",
			rf.me, args.Term, args.LastIncludedIndex)
	}
	if args.Term < rf.currentTerm {
		reply.Term = rf.currentTerm
		rf.persist()
		rf.mu.Unlock()
		return
	}
	if args.Term > rf.currentTerm {
		rf.currentTerm = args.Term
		rf.becomeFollower()
	}

	rf.log = make([]LogEntry, 1)
	rf.lastSnapshotIndex = args.LastIncludedIndex
	rf.lastSnapshotTerm = args.LastIncludedTerm
	rf.lastApplied = args.LastIncludedIndex
	rf.commitIndex = args.LastIncludedIndex
	rf.persister.SaveStateAndSnapshot(rf.getState(), args.Data)
	applyMsg := ApplyMsg{CommandValid: false, CommandIndex: args.LastIncludedIndex, IsSnapshot: true, Data: args.Data}
	rf.applyCh <- applyMsg
	rf.mu.Unlock()
}

//
// example code to send a RequestVote RPC to a server.
// server is the index of the target server in rf.peers[].
// expects RPC arguments in args.
// fills in *reply with RPC reply, so caller should
// pass &reply.
// the types of the args and reply passed to Call() must be
// the same as the types of the arguments declared in the
// handler function (including whether they are pointers).
//
// The labrpc package simulates a lossy network, in which servers
// may be unreachable, and in which requests and replies may be lost.
// Call() sends a request and waits for a reply. If a reply arrives
// within a timeout interval, Call() returns true; otherwise
// Call() returns false. Thus Call() may not return for a while.
// A false return can be caused by a dead server, a live server that
// can't be reached, a lost request, or a lost reply.
//
// Call() is guaranteed to return (perhaps after a delay) *except* if the
// handler function on the server side does not return.  Thus there
// is no need to implement your own timeouts around Call().
//
// look at the comments in ../labrpc/labrpc.go for more details.
//
// if you're having trouble getting RPC to work, check that you've
// capitalized all field names in structs passed over RPC, and
// that the caller passes the address of the reply struct with &, not
// the struct itself.
//
func (rf *Raft) sendRequestVote(server int, args *RequestVoteArgs, reply *RequestVoteReply) bool {
	ok := rf.peers[server].Call("Raft.RequestVote", args, reply)
	return ok
}

func (rf *Raft) sendAppendEntries(server int, args *AppendEntriesArgs, reply *AppendEntriesReply) bool {
	ok := rf.peers[server].Call("Raft.AppendEntries", args, reply)
	return ok
}

func (rf *Raft) sendInstallSnapshot(server int, args *InstallSnapshotArgs, reply *InstallSnapshotReply) bool {
	ok := rf.peers[server].Call("Raft.InstallSnapshot", args, reply)
	return ok
}

//
// the service using Raft (e.g. a k/v server) wants to start
// agreement on the next command to be appended to Raft's log. if this
// server isn't the leader, returns false. otherwise start the
// agreement and return immediately. there is no guarantee that this
// command will ever be committed to the Raft log, since the leader
// may fail or lose an election. even if the Raft instance has been killed,
// this function should return gracefully.
//
// the first return value is the index that the command will appear at
// if it's ever committed. the second return value is the current
// term. the third return value is true if this server believes it is
// the leader.
//
func (rf *Raft) Start(command interface{}) (int, int, bool) {
	rf.mu.Lock()
	index := len(rf.log) + rf.lastSnapshotIndex
	term := rf.currentTerm
	isLeader := rf.state == leader
	if !isLeader || rf.killed() {
		rf.mu.Unlock()
		return index, term, false
	}
	if PrintDebug {
		log.Printf("%d!: Append command to local log, index %d, term %d\n", rf.me, index, term)
	}
	logEntry := LogEntry{Command: command, Term: term}
	rf.log = append(rf.log, logEntry)
	rf.matchIndex[rf.me] = len(rf.log) - 1 + rf.lastSnapshotIndex
	rf.persist()
	rf.mu.Unlock()
	return index, term, isLeader
}

//
// the tester doesn't halt goroutines created by Raft after each test,
// but it does call the Kill() method. your code can use killed() to
// check whether Kill() has been called. the use of atomic avoids the
// need for a lock.
//
// the issue is that long-running goroutines use memory and may chew
// up CPU time, perhaps causing later tests to fail and generating
// confusing debug output. any goroutine with a long-running loop
// should call killed() to check whether it should stop.
//
func (rf *Raft) Kill() {
	atomic.StoreInt32(&rf.dead, 1)
	// Your code here, if desired.
}

func (rf *Raft) killed() bool {
	z := atomic.LoadInt32(&rf.dead)
	return z == 1
}

//
// the service or tester wants to create a Raft server. the ports
// of all the Raft servers (including this one) are in peers[]. this
// server's port is peers[me]. all the servers' peers[] arrays
// have the same order. persister is a place for this server to
// save its persistent state, and also initially holds the most
// recent saved state, if any. applyCh is a channel on which the
// tester or service expects Raft to send ApplyMsg messages.
// Make() must return quickly, so it should start goroutines
// for any long-running work.
//
func Make(peers []*labrpc.ClientEnd, me int,
	persister *Persister, applyCh chan ApplyMsg) *Raft {
	rf := &Raft{}
	rf.peers = peers
	rf.persister = persister
	rf.me = me

	// Your initialization code here (2A, 2B, 2C).
	rf.state = follower
	rf.currentTerm = 0
	rf.votedFor = -1
	rf.voteCount = 0
	rf.resetElectionTimer()

	rf.log = make([]LogEntry, 1)
	rf.commitIndex = 0
	rf.lastApplied = 0
	rf.lastSnapshotIndex = 0

	rf.applyCh = applyCh

	// initialize from state persisted before a crash
	rf.readPersist(persister.ReadRaftState())

	if PrintDebug {
		log.Printf("Server %d started\n", rf.me)
	}
	go rf.runElectionTimer()

	return rf
}

func (rf *Raft) runElectionTimer() {
	const tick = 10
	for {
		rf.mu.Lock()
		if rf.killed() {
			rf.mu.Unlock()
			return
		}
		if rf.timeSince < rf.electionTimer {
			rf.timeSince += tick
		} else if rf.state == follower || rf.state == candidate {
			rf.startElection()
			rf.resetElectionTimer()
		}
		rf.mu.Unlock()
		time.Sleep(tick * time.Millisecond)
	}
}

func (rf *Raft) resetElectionTimer() {
	const timerLower = 300
	const timerRange = 500
	rf.electionTimer = timerLower + rand.New(rand.NewSource(time.Now().UnixNano())).Intn(timerRange)
	rf.timeSince = 0
}

func (rf *Raft) startElection() {
	rf.state = candidate
	rf.currentTerm += 1
	rf.votedFor = rf.me
	rf.voteCount = 1
	if PrintDebug {
		log.Printf("%d: Start election for term %d\n", rf.me, rf.currentTerm)
	}
	for i := 0; i < len(rf.peers); i++ {
		if i == rf.me {
			continue
		}
		go rf.sendRequestVoteTo(i, rf.currentTerm)
	}
	rf.persist()
}

func (rf *Raft) sendRequestVoteTo(peer int, term int) {
	rf.mu.Lock()
	if rf.state != candidate || rf.currentTerm != term {
		if PrintDebug {
			log.Printf("%d: Abort sendRequestVoteTo %d, state: %v, term: %d, voting term: %d\n",
				rf.me, peer, rf.state, rf.currentTerm, term)
		}
		rf.mu.Unlock()
		return
	}
	lastLogIndex, lastLogTerm := rf.getPhysicalLastLogIndexAndTerm()
	args := RequestVoteArgs{Term: term, CandidateId: rf.me, LastLogIndex: lastLogIndex, LastLogTerm: lastLogTerm}
	reply := RequestVoteReply{}
	rf.mu.Unlock()
	ok := rf.sendRequestVote(peer, &args, &reply)
	if !ok {
		return
	}
	rf.mu.Lock()
	if rf.state != candidate || rf.currentTerm != term {
		if PrintDebug {
			log.Printf("%d: Abort sendRequestVoteTo %d, state: %v, term: %d, voting term: %d\n",
				rf.me, peer, rf.state, rf.currentTerm, term)
		}
		rf.mu.Unlock()
		return
	}
	if reply.Term == term && reply.VoteGranted {
		rf.voteCount++
		if PrintDebug {
			log.Printf("%d: Received vote from %d, %d current votes\n",
				rf.me, peer, rf.voteCount)
		}
		if rf.voteCount > len(rf.peers)/2 {
			rf.becomeLeader()
		}
	} else if rf.currentTerm < reply.Term {
		rf.currentTerm = reply.Term
		rf.becomeFollower()
	}
	rf.mu.Unlock()
}

func (rf *Raft) becomeLeader() {
	if PrintDebug {
		log.Printf("%d!: Become leader for term %d\n", rf.me, rf.currentTerm)
	}
	rf.state = leader
	// Initialize `nextIndex` && `matchIndex`
	rf.nextIndex = make([]int, len(rf.peers))
	rf.matchIndex = make([]int, len(rf.peers))
	for i := 0; i < len(rf.peers); i++ {
		rf.nextIndex[i] = len(rf.log) + rf.lastSnapshotIndex
		rf.matchIndex[i] = 0
	}
	go rf.executeAppendEntries()
}

func (rf *Raft) becomeFollower() {
	if PrintDebug {
		log.Printf("%d: Becomes follower\n", rf.me)
	}
	rf.state = follower
	rf.votedFor = -1
}

func (rf *Raft) executeAppendEntries() {
	for {
		rf.mu.Lock()
		if rf.killed() {
			rf.mu.Unlock()
			return
		}
		if rf.state != leader {
			rf.mu.Unlock()
			return
		}
		for i := 0; i < len(rf.peers); i++ {
			if i == rf.me {
				continue
			}
			go rf.sendAppendEntriesTo(i, rf.currentTerm)
		}
		rf.mu.Unlock()
		time.Sleep(110 * time.Millisecond)
	}
}

func (rf *Raft) sendAppendEntriesTo(peer int, term int) {
	rf.mu.Lock()
	if rf.state != leader || rf.currentTerm != term {
		if PrintDebug {
			log.Printf("%d: Abort sendAppendEntriesTo, state: %v, term: %d, sending term: %d\n",
				rf.me, rf.state, rf.currentTerm, term)
		}
		rf.mu.Unlock()
		return
	}
	// Prep args
	prevLogIndex := rf.nextIndex[peer] - 1
	physicalPrevLogIndex := prevLogIndex - rf.lastSnapshotIndex

	// Send InstallSnapshot
	if physicalPrevLogIndex < 0 {
		if PrintDebug {
			log.Printf("%d!: Send install snapshot to %d, term %d, last included index %d",
				rf.me, peer, term, rf.lastSnapshotIndex)
		}
		args := InstallSnapshotArgs{
			Term:              term,
			LeaderId:          rf.me,
			LastIncludedIndex: rf.lastSnapshotIndex,
			LastIncludedTerm:  rf.lastSnapshotTerm,
			Data:              rf.persister.ReadSnapshot(),
		}
		rf.mu.Unlock()
		reply := InstallSnapshotReply{}
		ok := rf.sendInstallSnapshot(peer, &args, &reply)
		if !ok {
			return
		}
		rf.mu.Lock()
		if rf.state != leader || rf.currentTerm != term {
			if PrintDebug {
				log.Printf("%d!: Abort receiving install snapshot replies, state: %v, term: %d, "+
					"sending term: %d\n",
					rf.me, rf.state, rf.currentTerm, term)
			}
			rf.mu.Unlock()
			return
		}
		if reply.Term > rf.currentTerm {
			rf.currentTerm = reply.Term
			rf.becomeFollower()
		} else {
			rf.nextIndex[peer] = rf.lastSnapshotIndex + 1
			rf.matchIndex[peer] = rf.lastSnapshotIndex
		}
		rf.mu.Unlock()
		return
	}

	var prevLogTerm int
	if physicalPrevLogIndex == 0 {
		prevLogTerm = rf.lastSnapshotTerm
	} else {
		prevLogTerm = rf.log[physicalPrevLogIndex].Term
	}
	startEntryIndex := physicalPrevLogIndex + 1
	entries := make([]LogEntry, len(rf.log[startEntryIndex:]))
	copy(entries, rf.log[startEntryIndex:])

	args := AppendEntriesArgs{
		Term:         term,
		LeaderId:     rf.me,
		PrevLogIndex: prevLogIndex,
		PrevLogTerm:  prevLogTerm,
		Entries:      entries,
		LeaderCommit: rf.commitIndex,
	}
	if PrintDebug {
		log.Printf("%d!: Send append entries to %d, term %d\n", rf.me, peer, term)
	}
	rf.mu.Unlock()
	reply := AppendEntriesReply{}
	ok := rf.sendAppendEntries(peer, &args, &reply)
	if !ok {
		return
	}
	rf.mu.Lock()
	if rf.state != leader || rf.currentTerm != term {
		if PrintDebug {
			log.Printf("%d: Abort receiving append entry replies, state: %v, term: %d, "+
				"sending term: %d\n",
				rf.me, rf.state, rf.currentTerm, term)
		}
		rf.mu.Unlock()
		return
	}
	if reply.Term == term && reply.Success {
		rf.nextIndex[peer] = prevLogIndex + len(entries) + 1
		rf.matchIndex[peer] = rf.nextIndex[peer] - 1
		rf.updateLeaderCommitIndex()
	} else if reply.Term > rf.currentTerm {
		rf.currentTerm = reply.Term
		rf.becomeFollower()
	} else if reply.Term == term && !reply.Success {
		if PrintDebug {
			log.Printf("%d: Received conflict index: %d, conflict term: %d; last snapshot index: %d\n",
				rf.me, reply.ConflictIndex, reply.ConflictTerm, rf.lastSnapshotIndex)
		}
		if reply.ConflictTerm != -1 && reply.ConflictIndex > rf.lastSnapshotIndex {
			termFound := false
			for i, entry := range rf.log {
				if !termFound && entry.Term == reply.ConflictTerm {
					termFound = true
				}
				if termFound && entry.Term != reply.ConflictTerm {
					rf.nextIndex[peer] = i + rf.lastSnapshotIndex - 1
					break
				}
			}
			if !termFound {
				rf.nextIndex[peer] = reply.ConflictIndex
			}
		} else {
			rf.nextIndex[peer] = reply.ConflictIndex
		}
	}
	rf.mu.Unlock()
}

func (rf *Raft) updateLeaderCommitIndex() {
	indexReplica := make(map[int]int)
	oldCommitIndex := rf.commitIndex
	for i := 0; i < len(rf.peers); i++ {
		matchIndex := rf.matchIndex[i]
		if matchIndex > rf.commitIndex {
			indexReplica[matchIndex] = indexReplica[matchIndex] + 1
			if indexReplica[matchIndex] > len(rf.peers)/2 && matchIndex < len(rf.log)+rf.lastSnapshotIndex &&
				rf.log[matchIndex-rf.lastSnapshotIndex].Term == rf.currentTerm {
				rf.commitIndex = matchIndex
			}
		}
	}
	if rf.commitIndex > oldCommitIndex {
		rf.commitFrom(oldCommitIndex + 1)
	}
}

func (rf *Raft) commitFrom(index int) {
	if PrintDebug {
		log.Printf("%d: Commit from %d to %d\n", rf.me, index, rf.commitIndex)
	}
	for i := index - rf.lastSnapshotIndex; i <= rf.commitIndex-rf.lastSnapshotIndex; i++ {
		applyMsg := ApplyMsg{
			CommandValid: true,
			Command:      rf.log[i].Command,
			CommandIndex: i + rf.lastSnapshotIndex,
			IsSnapshot:   false,
		}
		rf.applyCh <- applyMsg
	}
}

func (rf *Raft) cleanLogToInclusive(index int) {
	physicalLogIndex := index - rf.lastSnapshotIndex
	truncatedLog := make([]LogEntry, len(rf.log)-physicalLogIndex)
	for i := physicalLogIndex + 1; i < len(rf.log); i++ {
		truncatedLog[i-physicalLogIndex] = LogEntry{Command: rf.log[i].Command, Term: rf.log[i].Term}
	}
	rf.lastSnapshotIndex = index
	rf.lastSnapshotTerm = rf.log[physicalLogIndex].Term
	rf.log = truncatedLog
}

func (rf *Raft) getPhysicalLastLogIndexAndTerm() (int, int) {
	var lastLogIndex, lastLogTerm int
	if rf.lastSnapshotIndex > 0 && len(rf.log) == 1 {
		lastLogIndex = rf.lastSnapshotIndex
		lastLogTerm = rf.lastSnapshotTerm
	} else {
		lastLogIndex = len(rf.log) - 1 + rf.lastSnapshotIndex
		lastLogTerm = rf.log[len(rf.log)-1].Term
	}
	return lastLogIndex, lastLogTerm
}

func (rf *Raft) GetStateSize() int {
	rf.mu.Lock()
	defer rf.mu.Unlock()
	return rf.persister.RaftStateSize()
}

func (rf *Raft) SaveSnapshot(data []byte, logIndex int) {
	rf.mu.Lock()
	if PrintDebug {
		log.Printf("%d: Save snapshot at index %d", rf.me, logIndex)
	}
	rf.cleanLogToInclusive(logIndex)
	rf.persister.SaveStateAndSnapshot(rf.getState(), data)
	rf.mu.Unlock()
}

func (rf *Raft) GetSavedSnapshot() []byte {
	rf.mu.Lock()
	defer rf.mu.Unlock()
	return rf.persister.ReadSnapshot()
}
