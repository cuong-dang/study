package mr

import (
	"encoding/json"
	"fmt"
	"hash/fnv"
	"io/ioutil"
	"log"
	"net/rpc"
	"os"
	"sort"

	"github.com/google/uuid"
)

type WorkerStatus int
type WorkerUUID string

type Worker_ struct {
	UUID WorkerUUID
}

type WorkerTaskType int

const (
	Map WorkerTaskType = iota
	Reduce
)

type WorkerTask struct {
	ID       TaskID
	Kind     WorkerTaskType
	Filename string
	NReduce  int
}

type TaskReport struct {
	UUID         WorkerUUID
	TaskKind     WorkerTaskType
	MapTaskID    TaskID
	ReduceTaskID TaskID
	OFilename    string
}

//
// use ihash(key) % NReduce to choose the reduce
// task number for each KeyValue emitted by Map.
//
func ihash(key string) int {
	h := fnv.New32a()
	h.Write([]byte(key))
	return int(h.Sum32() & 0x7fffffff)
}

//
// main/mrworker.go calls this function.
//
func Worker(mapf func(string, string) []KeyValue,
	reducef func(string, []string) string) {
	worker := new(Worker_)
	worker.UUID = WorkerUUID(uuid.NewString())
	log.Printf("Start worker %v", worker.UUID)
	for {
		log.Println("Ask for task")
		task := new(WorkerTask)
		ok := call("Master.AssignTask", worker, task)
		if !ok { // all done
			return
		}
		log.Printf("Received new task %v from master\n", task.ID)
		switch task.Kind {
		case Map:
			oname, reduceID := doMap(task, mapf)
			log.Println("Send map task report to master")
			ok = call("Master.ReceiveTaskReport",
				TaskReport{worker.UUID, Map, task.ID, reduceID, oname}, new(struct{}))
			if !ok {
				log.Fatalf("Failed to report status for map task, file %v, worker %v",
					task.Filename, worker.UUID)
			}
		case Reduce:
			// doReduce(task, mapf)
		default:
			log.Fatalf("Unknown task %v\n", task.Kind)
		}
	}
}

func doMap(task *WorkerTask, mapf func(string, string) []KeyValue) (oname string, reduceID TaskID) {
	log.Printf("Run map task for file %v\n", task.Filename)
	file, err := os.Open(task.Filename)
	if err != nil {
		log.Fatalf("Cannot open %v", task.Filename)
	}
	defer file.Close()
	content, err := ioutil.ReadAll(file)
	if err != nil {
		log.Fatalf("Cannot read %v", task.Filename)
	}

	intermediate := mapf(task.Filename, string(content))
	sort.Sort(ByKey(intermediate))

	reduceID = TaskID(ihash(task.Filename) % task.NReduce)
	oname = fmt.Sprintf("mr-%v-%v", task.ID, reduceID)
	log.Printf("Write output to %v\n", oname)
	ofile, err := os.Create(oname)
	if err != nil {
		log.Fatalf("Cannot create %v", oname)
	}
	enc := json.NewEncoder(ofile)
	for _, kv := range intermediate {
		err := enc.Encode(&kv)
		if err != nil {
			log.Fatalf("Cannot write value %v", kv)
		}
	}
	return oname, reduceID
}

//
// send an RPC request to the master, wait for the response.
// usually returns true.
// returns false if something goes wrong.
//
func call(rpcname string, args interface{}, reply interface{}) bool {
	// c, err := rpc.DialHTTP("tcp", "127.0.0.1"+":1234")
	sockname := masterSock()
	c, err := rpc.DialHTTP("unix", sockname)
	if err != nil {
		log.Fatal("dialing:", err)
	}
	defer c.Close()

	err = c.Call(rpcname, args, reply)
	if err == nil {
		return true
	}

	fmt.Println(err)
	return false
}

//
// Map functions return a slice of KeyValue.
//
type KeyValue struct {
	Key   string
	Value string
}

// for sorting by key.
type ByKey []KeyValue

// for sorting by key.
func (a ByKey) Len() int           { return len(a) }
func (a ByKey) Swap(i, j int)      { a[i], a[j] = a[j], a[i] }
func (a ByKey) Less(i, j int) bool { return a[i].Key < a[j].Key }
