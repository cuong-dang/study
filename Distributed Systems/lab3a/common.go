package kvraft

import (
	"fmt"
	"math/rand"
	"time"
)

const (
	OK             = "OK"
	ErrNoKey       = "ErrNoKey"
	ErrWrongLeader = "ErrWrongLeader"
)

type Err string

// Put or Append
type PutAppendArgs struct {
	RequestId string
	Key       string
	Value     string
	Op        string // "Put" or "Append"
}

type PutAppendReply struct {
	Err Err
}

type GetArgs struct {
	RequestId string
	Key       string
}

type GetReply struct {
	Err   Err
	Value string
}

func makeId() string {
	return fmt.Sprintf("%v-%v", time.Now().Unix(), rand.Int())
}
