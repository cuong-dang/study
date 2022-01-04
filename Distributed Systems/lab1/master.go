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

type WorkerStatus int

const (
	working WorkerStatus = iota
	available
	dead
)

type Master struct {
	mu                sync.Mutex
	mapTasks          map[int]MasterMapTask
	mapTasksIdle      int
	mapTasksCompleted int
	workerStatus      map[string]WorkerStatus
	reduceTasks       []string
	nReduce           int
}

type MasterMapTask struct {
	filename         string
	status           MapTaskStatus
	startedTimestamp int64
	worker           Worker_
}

type MapTaskStatus int

const (
	idle MapTaskStatus = iota
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

	if m.mapTasksIdle > 0 {
		log.Printf("There are %d idle map tasks\n", m.mapTasksIdle)
		for i, task := range m.mapTasks {
			if task.status == idle {
				log.Printf("Assign map task, file %v\n", task.filename)
				reply.ID = i
				reply.Kind = Map
				reply.Filename = task.filename
				reply.NReduce = m.nReduce

				task.status = inProgress
				task.startedTimestamp = time.Now().Unix()
				m.mapTasksIdle--
				m.workerStatus[worker.UUID] = working
				break
			}
		}
	} else if m.mapTasksCompleted == len(m.mapTasks) {
		log.Printf("")
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
		task := m.mapTasks[report.TaskID]
		task.status = completed
		m.mapTasks[report.TaskID] = task
		m.mapTasksCompleted++
		log.Printf("Completed %v/%v map tasks\n", m.mapTasksCompleted, len(m.mapTasks))
		m.reduceTasks = append(m.reduceTasks, report.OFilename)
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
	m.mapTasks = make(map[int]MasterMapTask)
	m.workerStatus = make(map[string]WorkerStatus)
	for i, filename := range files {
		m.mapTasks[i] = MasterMapTask{filename, idle, 0, Worker_{}}
	}
	m.mapTasksIdle = len(m.mapTasks)
	log.Printf("Discovered %v files", m.mapTasksIdle)
	m.nReduce = nReduce
	log.Printf("%v reduce tasks specified\n", nReduce)

	m.server()
	return &m
}
