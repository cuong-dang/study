package kvraft

const (
	OK              = "OK"
	ErrWrongLeader  = "ErrWrongLeader"
	ErrNotCommitted = "NotCommitted"
)

type Err string

// Put or Append
type PutAppendArgs struct {
	RequestId int64
	Key       string
	Value     string
	Op        string // "Put" or "Append"
}

type PutAppendReply struct {
	Err Err
}

type GetArgs struct {
	RequestId int64
	Key       string
}

type GetReply struct {
	Err   Err
	Value string
}
