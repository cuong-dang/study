package shardmaster

import (
	"crypto/sha1"
	"fmt"
	"io"
	"time"
)

//
// Master shard server: assigns shards to replication groups.
//
// RPC interface:
// Join(servers) -- add a set of groups (gid -> server-list mapping).
// Leave(gids) -- delete a set of groups.
// Move(shard, gid) -- hand off one shard from current owner to gid.
// Query(num) -> fetch Config # num, or latest config if num==-1.
//
// A Config (configuration) describes a set of replica groups, and the
// replica group responsible for each shard. Configs are numbered. Config
// #0 is the initial configuration, with no groups and all shards
// assigned to group 0 (the invalid group).
//
// You will need to add fields to the RPC argument structs.
//

// The number of shards.
const NShards = 10

// A configuration -- an assignment of shards to groups.
// Please don't change this.
type Config struct {
	Num    int              // config number
	Shards [NShards]int     // shard -> gid
	Groups map[int][]string // gid -> servers[]
}

const (
	OK = "OK"
)

type Err string

// RequestCommon contains requests' common data.
type RequestCommon struct {
	RequestId int64
}

// JoinArgs contains arguments for Join request.
type JoinArgs struct {
	RequestCommon
	Servers map[int][]string // new GID -> servers mappings
}

type JoinReply struct {
	WrongLeader bool
	Err         Err
}

// LeaveArgs contains arguments for Leave requests.
type LeaveArgs struct {
	RequestCommon
	GIDs []int
}

type LeaveReply struct {
	WrongLeader bool
	Err         Err
}

// MoveArgs contains arguments for Move requests.
type MoveArgs struct {
	RequestCommon
	Shard int
	GID   int
}

type MoveReply struct {
	WrongLeader bool
	Err         Err
}

// QueryArgs contains arguments for Query requests.
type QueryArgs struct {
	RequestCommon
	Num int // desired config number
}

// QueryReply contains Query reply data from Shard Master servers.
type QueryReply struct {
	WrongLeader bool
	Err         Err
	Config      Config
}

// GenProcessId generates a simple uuid to identify processes. This is only
// for debug purposes.
func GenProcessId() string {
	const IdLen = 7
	h := sha1.New()
	io.WriteString(h, time.Now().String())
	return string([]rune(fmt.Sprint(h.Sum(nil)))[:IdLen])
}
