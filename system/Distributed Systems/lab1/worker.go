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
	AllDone
)

type WorkerTask struct {
	ID              TaskID
	Kind            WorkerTaskType
	MapFilename     string
	ReduceFilenames []string
	NReduce         int
}

type TaskReport struct {
	UUID        WorkerUUID
	TaskKind    WorkerTaskType
	TaskID      TaskID
	ReduceTasks map[TaskID][]string
	ReduceOFile string
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
		taskKindString := "map"
		if task.Kind == Reduce {
			taskKindString = "reduce"
		}
		log.Printf("Received new %v task %v from master\n", taskKindString, task.ID)
		switch task.Kind {
		case Map:
			reduceTasks := doMap(task, mapf)
			log.Printf("Send map task ID %v report to master\n", task.ID)
			ok = call("Master.ReceiveTaskReport",
				TaskReport{worker.UUID, Map, task.ID, reduceTasks, ""}, new(struct{}))
			if !ok {
				log.Fatalf("Failed to report status for map task, file %v, worker %v",
					task.MapFilename, worker.UUID)
			}
		case Reduce:
			ofile := doReduce(task, reducef)
			log.Printf("Send reduce task ID %v report to master\n", task.ID)
			ok = call("Master.ReceiveTaskReport",
				TaskReport{worker.UUID, Reduce, task.ID, nil, ofile}, new(struct{}))
			if !ok {
				log.Fatalf("Failed to report status for reduce task, file %v, worker %v",
					task.ReduceFilenames, worker.UUID)
			}
		case AllDone:
			log.Println("ALL DONE!")
			return
		default:
			log.Fatalf("Unknown task %v\n", task.Kind)
		}
	}
}

func doMap(task *WorkerTask, mapf func(string, string) []KeyValue) (reduceTasks map[TaskID][]string) {
	log.Printf("Run map task for file %v\n", task.MapFilename)
	file, err := os.Open(task.MapFilename)
	if err != nil {
		log.Fatalf("Cannot open %v", task.MapFilename)
	}
	defer file.Close()
	content, err := ioutil.ReadAll(file)
	if err != nil {
		log.Fatalf("Cannot read %v", task.MapFilename)
	}

	intermediate := mapf(task.MapFilename, string(content))
	sort.Sort(ByKey(intermediate))
	reduceIDMap := make(map[TaskID][]KeyValue)

	for _, kv := range intermediate {
		reduceID := TaskID(ihash(kv.Key) % task.NReduce)
		kva, ok := reduceIDMap[reduceID]
		if !ok {
			kva = []KeyValue{kv}
		} else {
			kva = append(kva, kv)
		}
		reduceIDMap[reduceID] = kva
	}

	reduceTasks = make(map[TaskID][]string)
	onames := []string{}
	for reduceID, kva := range reduceIDMap {
		oname := fmt.Sprintf("mr-%v-%v", task.ID, reduceID)
		onames = append(onames, oname)
		ofile, err := ioutil.TempFile("", "temp")
		if err != nil {
			log.Fatalf("Cannot create %v", oname)
		}
		enc := json.NewEncoder(ofile)
		for _, kv := range kva {
			err := enc.Encode(&kv)
			if err != nil {
				log.Fatalf("Cannot write value %v", kv)
			}
		}
		os.Rename(ofile.Name(), oname)
		reduceTasks[reduceID] = append(reduceTasks[reduceID], oname)
	}
	log.Printf("Wrote output to %v\n", onames)
	return reduceTasks
}

func doReduce(task *WorkerTask, reducef func(string, []string) string) (oname string) {
	log.Printf("Run reduce task for files %v\n", task.ReduceFilenames)
	intermediate := []KeyValue{}
	for _, filename := range task.ReduceFilenames {
		file, err := os.Open(filename)
		if err != nil {
			log.Fatalf("Cannot open %v", filename)
		}
		defer file.Close()

		kva := []KeyValue{}
		dec := json.NewDecoder(file)
		for {
			kv := KeyValue{}
			if err := dec.Decode(&kv); err != nil {
				break
			}
			kva = append(kva, kv)
		}
		intermediate = append(intermediate, kva...)
	}

	oname = fmt.Sprintf("mr-out-%v", task.ID)
	log.Printf("Write output to %v\n", oname)
	ofile, err := ioutil.TempFile("", "temp")
	if err != nil {
		log.Fatalf("Fail to create file %v\n", oname)
	}
	sort.Sort(ByKey(intermediate))
	i := 0
	for i < len(intermediate) {
		j := i + 1
		for j < len(intermediate) && intermediate[i].Key == intermediate[j].Key {
			j++
		}
		values := []string{}
		for k := i; k < j; k++ {
			values = append(values, intermediate[k].Value)
		}
		output := reducef(intermediate[i].Key, values)
		fmt.Fprintf(ofile, "%v %v\n", intermediate[i].Key, output)
		i = j
	}
	os.Rename(ofile.Name(), oname)
	return oname
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
