package kvraft

import (
	"crypto/rand"
	"math/big"

	"../labrpc"
)

type Clerk struct {
	id      int64
	servers []*labrpc.ClientEnd
	leader  int
}

func nrand() int64 {
	max := big.NewInt(int64(1) << 62)
	bigx, _ := rand.Int(rand.Reader, max)
	x := bigx.Int64()
	return x
}

func MakeClerk(servers []*labrpc.ClientEnd) *Clerk {
	ck := new(Clerk)
	ck.id = nrand()
	ck.servers = servers
	ck.leader = 0
	return ck
}

//
// fetch the current value for a key.
// returns "" if the key does not exist.
// keeps trying forever in the face of all other errors.
//
// you can send an RPC with code like this:
// ok := ck.servers[i].Call("KVServer.Get", &args, &reply)
//
// the types of args and reply (including whether they are pointers)
// must match the declared types of the RPC handler function's
// arguments. and reply must be passed as a pointer.
//
func (ck *Clerk) Get(key string) string {
	args := GetArgs{RequestId: nrand(), Key: key}
	for {
		reply := GetReply{}
		DPrintf("%d --Get--> %d: %v\n", ck.id, ck.leader, args)
		ok := ck.servers[ck.leader].Call("KVServer.Get", &args, &reply)
		if ck.handleResponse(ok, reply.Err, "Get") {
			return reply.Value
		}
		ck.leader = (ck.leader + 1) % len(ck.servers)
	}
}

//
// shared by Put and Append.
//
// you can send an RPC with code like this:
// ok := ck.servers[i].Call("KVServer.PutAppend", &args, &reply)
//
// the types of args and reply (including whether they are pointers)
// must match the declared types of the RPC handler function's
// arguments. and reply must be passed as a pointer.
//
func (ck *Clerk) PutAppend(key string, value string, op string) {
	args := PutAppendArgs{RequestId: nrand(), Key: key, Value: value, Op: op}
	for {
		reply := PutAppendReply{}
		DPrintf("%d --PutAppend--> %d: %v\n", ck.id, ck.leader, args)
		ok := ck.servers[ck.leader].Call("KVServer.PutAppend", &args, &reply)
		if ck.handleResponse(ok, reply.Err, "PutAppend") {
			return
		}
		ck.leader = (ck.leader + 1) % len(ck.servers)
	}
}

func (ck *Clerk) Put(key string, value string) {
	ck.PutAppend(key, value, "Put")
}
func (ck *Clerk) Append(key string, value string) {
	ck.PutAppend(key, value, "Append")
}

func (ck *Clerk) handleResponse(ok bool, err Err, requestType string) bool {
	if ok && err == OK {
		DPrintf("%d <--%v-- %d: OK\n", ck.id, requestType, ck.leader)
		return true
	} else if ok && err == ErrWrongLeader {
		DPrintf("%d <--%v-- %d: Not leader\n", ck.id, requestType, ck.leader)
	} else if ok && err == ErrNotCommitted {
		DPrintf("%d <--%v-- %d: Not committed\n", ck.id, requestType, ck.leader)
	} else {
		DPrintf("%d <--%v-- %d: No responses from server\n", ck.id, requestType, ck.leader)
	}
	return false
}
