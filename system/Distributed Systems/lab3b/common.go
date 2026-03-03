package kvraft

const (
	OK              = "OK"
	ErrWrongLeader  = "ErrWrongLeader"
	ErrNotCommitted = "NotCommitted"
)

type Err string

// Put or Append
type PutAppendArgs struct {
	RequestId     int64
	Key           string
	Value         string
	Op            string // "Put" or "Append"
	LastRequestId int64
}

type PutAppendReply struct {
	Err Err
}

type GetArgs struct {
	RequestId     int64
	Key           string
	LastRequestId int64
}

type GetReply struct {
	Err   Err
	Value string
}
