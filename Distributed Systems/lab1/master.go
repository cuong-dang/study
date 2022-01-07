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
	cond         *sync.Cond
	mapTasks     MasterMapTasks
	reduceTasks  MasterReduceTasks
	workerStatus map[WorkerUUID]WorkerStatus
}

type MasterMapTasks struct {
	tasks map[TaskID]MasterMapTask
	TaskNumbers
}

type MasterReduceTasks struct {
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
				reply.ID = i
				reply.Kind = Map
				reply.MapFilename = task.filename
				reply.NReduce = m.reduceTasks.total

				task.status = inProgress
				task.startedTimestamp = time.Now().Unix()
				m.mapTasks.idle--
				m.workerStatus[worker.UUID] = working
				break
			}
		}
		return nil
	} else if m.mapTasks.completed != m.mapTasks.total {
		log.Println("Wait for all map tasks to complete")
		m.cond.Wait()
	}
	log.Printf("There are %d idle reduce tasks\n", m.reduceTasks.idle)
	for i, task := range m.reduceTasks.tasks {
		if task.status == idle {
			log.Printf("Assign reduce task, files %v\n", task.filenames)
			reply.ID = i
			reply.Kind = Reduce
			reply.ReduceFilenames = task.filenames

			task.status = inProgress
			task.startedTimestamp = time.Now().Unix()
			m.reduceTasks.idle--
			m.workerStatus[worker.UUID] = working
			break
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
		log.Println("Report for map task")
		if m.workerStatus[report.UUID] == dead {
			log.Println("Ignore report from worker marked dead")
			return nil
		}

		mapTask := m.mapTasks.tasks[report.MapTaskID]
		mapTask.status = completed
		m.mapTasks.tasks[report.MapTaskID] = mapTask
		m.mapTasks.completed++
		log.Printf("Completed %v/%v map tasks\n", m.mapTasks.completed, m.mapTasks.total)

		for taskID, filenames := range report.ReduceTasks {
			log.Printf("There are %v files for reduce ID %v\n", len(filenames), taskID)
			reduceTask, ok := m.reduceTasks.tasks[taskID]
			if !ok { // new reduce task
				m.reduceTasks.idle++
				reduceTask = MasterReduceTask{[]string{}, Task{idle, 0, Worker_{}}}
				copy(reduceTask.filenames, filenames)
			} else {
				reduceTask.filenames = append(reduceTask.filenames, filenames...)
			}
			m.reduceTasks.tasks[taskID] = reduceTask
		}

		m.workerStatus[report.UUID] = available
	}
	if m.mapTasks.completed == m.mapTasks.total {
		log.Println("All map tasks completed")
		m.cond.Broadcast()
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
	log.Println("Make master")
	m := Master{}
	m.cond = sync.NewCond(&m.mu)

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
