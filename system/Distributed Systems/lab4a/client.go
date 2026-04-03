package shardmaster

import "../labrpc"
import "time"
import "crypto/rand"
import "math/big"

// A Clerk is what clients use to talk to a group of Shard Master servers.
type Clerk struct {
	id      string
	servers []*labrpc.ClientEnd
}

// nrand generates a random number used as a request id.
func nrand() int64 {
	max := big.NewInt(int64(1) << 62)
	bigx, _ := rand.Int(rand.Reader, max)
	x := bigx.Int64()
	return x
}

// MakeClerk constructs and initializes a Clerk.
func MakeClerk(servers []*labrpc.ClientEnd) *Clerk {
	ck := new(Clerk)
	ck.id = GenProcessId()
	ck.servers = servers
	return ck
}

// Query sends a Query request to Shard Master servers for a configuration
// number num.
func (ck *Clerk) Query(num int) Config {
	args := &QueryArgs{RequestCommon: RequestCommon{RequestId: nrand()}, Num: num}
	for {
		for _, srv := range ck.servers {
			var reply QueryReply
			ok := srv.Call("ShardMaster.Query", args, &reply)
			if ok && reply.WrongLeader == false {
				return reply.Config
			}
		}
		time.Sleep(100 * time.Millisecond)
	}
}

// Join sends a Join request to Shard Master servers.
func (ck *Clerk) Join(servers map[int][]string) {
	args := &JoinArgs{RequestCommon: RequestCommon{RequestId: nrand()}, Servers: servers}
	for {
		for _, srv := range ck.servers {
			var reply JoinReply
			ok := srv.Call("ShardMaster.Join", args, &reply)
			if ok && reply.WrongLeader == false {
				return
			}
		}
		time.Sleep(100 * time.Millisecond)
	}
}

// Leave sends a Leave request to Shard Master servers.
func (ck *Clerk) Leave(gids []int) {
	args := &LeaveArgs{RequestCommon: RequestCommon{RequestId: nrand()}, GIDs: gids}
	for {
		for _, srv := range ck.servers {
			var reply LeaveReply
			ok := srv.Call("ShardMaster.Leave", args, &reply)
			if ok && reply.WrongLeader == false {
				return
			}
		}
		time.Sleep(100 * time.Millisecond)
	}
}

// Move sends a Move request to Shard Master servers.
func (ck *Clerk) Move(shard int, gid int) {
	args := &MoveArgs{RequestCommon: RequestCommon{RequestId: nrand()}, Shard: shard, GID: gid}
	for {
		for _, srv := range ck.servers {
			var reply MoveReply
			ok := srv.Call("ShardMaster.Move", args, &reply)
			if ok && reply.WrongLeader == false {
				return
			}
		}
		time.Sleep(100 * time.Millisecond)
	}
}
