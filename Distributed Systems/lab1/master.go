package mr

import (
	"log"
	"net"
	"net/http"
	"net/rpc"
	"os"
	"sync"
	"time"
)

const (
	working WorkerStatus = iota
	available
	dead
)

type Master struct {
	mu           sync.Mutex
	mapTasks     MapTasks
	reduceTasks  ReduceTasks
	workerStatus map[WorkerUUID]WorkerStatus
}

type MapTasks struct {
	tasks map[TaskID]MasterMapTask
	TaskNumbers
}

type ReduceTasks struct {
	tasks map[TaskID]MasterReduceTask
	TaskNumbers
}

type TaskNumbers struct {
	total     int
	idle      int
	completed int
}

type MasterMapTask struct {
	filename string
	Task
}

type MasterReduceTask struct {
	filenames []string
	Task
}

type Task struct {
	status           TaskStatus
	startedTimestamp int64
	worker           Worker_
}

type TaskID int
type TaskStatus int

const (
	idle TaskStatus = iota
	inProgress
	completed
)

//
// assign task to worker
//
func (m *Master) AssignTask(worker Worker_, reply *WorkerTask) error {
	log.Printf("Worker %v requested task\n", worker.UUID)
	m.mu.Lock()
	defer m.mu.Unlock()

	if m.mapTasks.idle > 0 {
		log.Printf("There are %d idle map tasks\n", m.mapTasks.idle)
		for i, task := range m.mapTasks.tasks {
			if task.status == idle {
				log.Printf("Assign map task, file %v\n", task.filename)
				reply.ID = TaskID(i)
				reply.Kind = Map
				reply.Filename = task.filename
				reply.NReduce = m.reduceTasks.total

				task.status = inProgress
				task.startedTimestamp = time.Now().Unix()
				m.mapTasks.idle--
				m.workerStatus[worker.UUID] = working
				break
			}
		}
	} else if m.mapTasks.completed == m.mapTasks.total {
		if m.reduceTasks.idle != m.reduceTasks.total {
			log.Fatal("inconsistent reduce tasks!")
		}
	}
	return nil
}

//
// receive report from worker
//
func (m *Master) ReceiveTaskReport(report TaskReport, reply *struct{}) error {
	log.Printf("Received task report from worker %v\n", report.UUID)
	m.mu.Lock()
	defer m.mu.Unlock()

	switch report.TaskKind {
	case Map:
		log.Printf("Report for map task, ofile %v\n", report.OFilename)
		if m.workerStatus[report.UUID] == dead {
			log.Println("Ignore report from worker marked dead")
			return nil
		}

		mapTask := m.mapTasks.tasks[report.MapTaskID]
		mapTask.status = completed
		m.mapTasks.tasks[report.MapTaskID] = mapTask
		m.mapTasks.completed++
		log.Printf("Completed %v/%v map tasks\n", m.mapTasks.completed, m.mapTasks.total)

		reduceTask, ok := m.reduceTasks.tasks[report.ReduceTaskID]
		if !ok { // new reduce task
			m.reduceTasks.idle++
			reduceTask = MasterReduceTask{[]string{report.OFilename}, Task{idle, 0, Worker_{}}}
		} else {
			reduceTask.filenames = append(reduceTask.filenames, report.OFilename)
			m.reduceTasks.tasks[report.ReduceTaskID] = reduceTask
		}

		m.workerStatus[report.UUID] = available
	}
	return nil
}

//
// start a thread that listens for RPCs from worker.go
//
func (m *Master) server() {
	rpc.Register(m)
	rpc.HandleHTTP()
	//l, e := net.Listen("tcp", ":1234")
	sockname := masterSock()
	os.Remove(sockname)
	l, e := net.Listen("unix", sockname)
	if e != nil {
		log.Fatal("listen error:", e)
	}
	go http.Serve(l, nil)
}

//
// main/mrmaster.go calls Done() periodically to find out
// if the entire job has finished.
//
func (m *Master) Done() bool {
	ret := false

	// Your code here.

	return ret
}

//
// create a Master.
// main/mrmaster.go calls this function.
// nReduce is the number of reduce tasks to use.
//
func MakeMaster(files []string, nReduce int) *Master {
	m := Master{}

	log.Println("Make master")
	m.mapTasks.tasks = make(map[TaskID]MasterMapTask)
	m.reduceTasks.tasks = make(map[TaskID]MasterReduceTask)
	m.workerStatus = make(map[WorkerUUID]WorkerStatus)
	for i, filename := range files {
		m.mapTasks.tasks[TaskID(i)] = MasterMapTask{filename, Task{idle, 0, Worker_{}}}
	}
	m.mapTasks.total = len(m.mapTasks.tasks)
	m.mapTasks.idle = m.mapTasks.total
	log.Printf("Discovered %v files", m.mapTasks.total)
	m.reduceTasks.total = nReduce
	log.Printf("%v reduce tasks specified\n", m.reduceTasks.total)

	m.server()
	return &m
}
