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
	"log"
	"math/rand"
	"sync"
	"sync/atomic"
	"time"

	"../labrpc"
)

// import "bytes"
// import "../labgob"

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

//
// save Raft's persistent state to stable storage,
// where it can later be retrieved after a crash and restart.
// see paper's Figure 2 for a description of what should be persistent.
//
func (rf *Raft) persist() {
	// Your code here (2C).
	// Example:
	// w := new(bytes.Buffer)
	// e := labgob.NewEncoder(w)
	// e.Encode(rf.xxx)
	// e.Encode(rf.yyy)
	// data := w.Bytes()
	// rf.persister.SaveRaftState(data)
}

//
// restore previously persisted state.
//
func (rf *Raft) readPersist(data []byte) {
	if data == nil || len(data) < 1 { // bootstrap without any state?
		return
	}
	// Your code here (2C).
	// Example:
	// r := bytes.NewBuffer(data)
	// d := labgob.NewDecoder(r)
	// var xxx
	// var yyy
	// if d.Decode(&xxx) != nil ||
	//    d.Decode(&yyy) != nil {
	//   error...
	// } else {
	//   rf.xxx = xxx
	//   rf.yyy = yyy
	// }
}

//
// example RequestVote RPC arguments structure.
// field names must start with capital letters!
//
type RequestVoteArgs struct {
	Term        int
	CandidateId int
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
	Term int
}

type AppendEntriesReply struct {
	Term int
}

//
// example RequestVote RPC handler.
//
func (rf *Raft) RequestVote(args *RequestVoteArgs, reply *RequestVoteReply) {
	rf.mu.Lock()
	log.Printf("%d: RequestVote RPC recevied from %d, term %d\n",
		rf.me, args.CandidateId, args.Term)
	if args.Term > rf.currentTerm {
		rf.currentTerm = args.Term
		rf.becomeFollower()
	} else if args.Term < rf.currentTerm {
		log.Printf("%d: Vote declined to %d\n", rf.me, args.CandidateId)
		reply.VoteGranted = false
	}
	if rf.state == follower && (rf.votedFor == -1 || rf.votedFor == args.CandidateId) {
		log.Printf("%d: Vote granted to %d\n", rf.me, args.CandidateId)
		rf.votedFor = args.CandidateId
		reply.VoteGranted = true
		rf.resetElectionTimer()
	}
	reply.Term = rf.currentTerm
	rf.mu.Unlock()
}

func (rf *Raft) AppendEntries(args *AppendEntriesArgs, reply *AppendEntriesReply) {
	rf.mu.Lock()
	log.Printf("%d: AppendEntries RPC recevied, term %d\n", rf.me, args.Term)
	if args.Term > rf.currentTerm {
		rf.currentTerm = args.Term
		rf.becomeFollower()
	} else if args.Term == rf.currentTerm {
		if rf.state == candidate {
			rf.becomeFollower()
		} else if rf.state != leader {
			rf.resetElectionTimer()
		}
	}
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
	index := -1
	term := -1
	isLeader := true

	// Your code here (2B).

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

	// initialize from state persisted before a crash
	rf.readPersist(persister.ReadRaftState())

	log.Printf("Server %d started\n", rf.me)
	go rf.runElectionTimer()

	return rf
}

func (rf *Raft) runElectionTimer() {
	for {
		if rf.killed() {
			return
		}
		rf.mu.Lock()
		if rf.timeSince < rf.electionTimer {
			rf.timeSince += 100
		} else if rf.state == follower || rf.state == candidate {
			go rf.startElection()
		} else {
			rf.resetElectionTimer()
		}
		rf.mu.Unlock()
		time.Sleep(100 * time.Millisecond)
	}
}

func (rf *Raft) resetElectionTimer() {
	rf.electionTimer = 300 + rand.New(rand.NewSource(time.Now().UnixNano())).Intn(300)
	rf.timeSince = 0
}

func (rf *Raft) startElection() {
	rf.mu.Lock()
	rf.state = candidate
	rf.currentTerm += 1
	rf.votedFor = rf.me
	rf.voteCount = 1
	rf.resetElectionTimer()
	log.Printf("%d: Start election for term %d\n", rf.me, rf.currentTerm)
	rf.mu.Unlock()
	for i := 0; i < len(rf.peers); i++ {
		if i == rf.me {
			continue
		}
		go func(i int) {
			rf.mu.Lock()
			args := RequestVoteArgs{Term: rf.currentTerm, CandidateId: rf.me}
			reply := RequestVoteReply{}
			rf.mu.Unlock()
			ok := rf.sendRequestVote(i, &args, &reply)
			rf.mu.Lock()
			if ok && rf.state == candidate {
				if reply.VoteGranted {
					rf.voteCount++
					log.Printf("%d: Received vote from %d, %d current votes\n",
						rf.me, i, rf.voteCount)
					if rf.voteCount > len(rf.peers)/2 {
						rf.state = leader
						rf.becomeLeader()
					}
				} else if rf.currentTerm < reply.Term {
					rf.currentTerm = reply.Term
					rf.becomeFollower()
				}
			}
			rf.mu.Unlock()
		}(i)
	}
}

func (rf *Raft) becomeLeader() {
	log.Printf("%d: Become leader for term %d\n", rf.me, rf.currentTerm)
	go rf.sendHeartbeats()

}

func (rf *Raft) becomeFollower() {
	log.Printf("%d: Becomes follower\n", rf.me)
	rf.state = follower
	rf.votedFor = -1
	rf.resetElectionTimer()
}

func (rf *Raft) sendHeartbeats() {
	args := AppendEntriesArgs{Term: rf.currentTerm}
	for {
		if rf.killed() {
			return
		}
		rf.mu.Lock()
		if rf.state != leader {
			rf.mu.Unlock()
			break
		}
		log.Printf("%d: Send heartbeats\n", rf.me)
		for i := 0; i < len(rf.peers); i++ {
			if i == rf.me {
				continue
			}
			go func(i int) {
				reply := AppendEntriesReply{}
				rf.sendAppendEntries(i, &args, &reply)
			}(i)
		}
		rf.mu.Unlock()
		time.Sleep(150 * time.Millisecond)
	}
}
