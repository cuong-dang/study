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

const deadWorkerThresholdSeconds = 10

type Master struct {
	mu           sync.Mutex
	cond         *sync.Cond
	mapTasks     MasterMapTasks
	reduceTasks  MasterReduceTasks
	workerStatus map[WorkerUUID]WorkerStatus
	taskReseted  bool
}

type MasterMapTasks struct {
	tasks map[TaskID]MasterMapTask
	TaskStats
}

type MasterReduceTasks struct {
	tasks map[TaskID]MasterReduceTask
	TaskStats
}

type TaskStats struct {
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

	for {
		if m.mapTasks.idle > 0 {
			log.Printf("There are %v idle map tasks\n", m.mapTasks.idle)
			for taskID, task := range m.mapTasks.tasks {
				if task.status == idle {
					log.Printf("Assign map task ID %v, file %v\n", taskID, task.filename)
					reply.ID = taskID
					reply.Kind = Map
					reply.MapFilename = task.filename
					reply.NReduce = m.reduceTasks.total

					task.status = inProgress
					task.startedTimestamp = time.Now().Unix()
					task.worker.UUID = worker.UUID
					m.mapTasks.tasks[taskID] = task
					m.mapTasks.idle--
					m.workerStatus[worker.UUID] = working
					break
				}
			}
			return nil
		}
		for m.mapTasks.completed != m.mapTasks.total && !m.taskReseted {
			log.Println("Wait for all map tasks to complete")
			m.cond.Wait()
		}
		for m.reduceTasks.idle == 0 && m.reduceTasks.completed != m.reduceTasks.total &&
			!m.taskReseted {
			log.Println("No more tasks right now, waiting...")
			m.cond.Wait()
		}
		if m.taskReseted {
			m.taskReseted = false
			continue
		}
		if m.reduceTasks.completed == m.reduceTasks.total {
			reply.Kind = AllDone
			return nil
		}
		log.Printf("There are %v idle reduce tasks\n", m.reduceTasks.idle)
		for taskID, task := range m.reduceTasks.tasks {
			if task.status == idle {
				log.Printf("Assign reduce task %v, files %v\n", taskID, task.filenames)
				reply.ID = taskID
				reply.Kind = Reduce
				reply.ReduceFilenames = task.filenames

				task.status = inProgress
				task.startedTimestamp = time.Now().Unix()
				task.worker.UUID = worker.UUID
				m.reduceTasks.tasks[taskID] = task
				m.reduceTasks.idle--
				m.workerStatus[worker.UUID] = working
				break
			}
		}
		return nil
	}
}

//
// receive report from worker
//
func (m *Master) ReceiveTaskReport(report TaskReport, reply *struct{}) error {
	log.Printf("Received task report from worker %v\n", report.UUID)
	m.mu.Lock()
	defer m.mu.Unlock()

	if m.workerStatus[report.UUID] == dead {
		log.Println("Ignore report from worker marked dead")
		return nil
	}

	switch report.TaskKind {
	case Map:
		log.Printf("Report for map task ID %v\n", report.TaskID)
		mapTask := m.mapTasks.tasks[report.TaskID]
		mapTask.status = completed
		m.mapTasks.tasks[report.TaskID] = mapTask
		m.mapTasks.completed++
		log.Printf("Completed %v/%v map tasks\n", m.mapTasks.completed, m.mapTasks.total)

		for taskID, filenames := range report.ReduceTasks {
			reduceTask, ok := m.reduceTasks.tasks[taskID]
			if !ok { // new reduce task
				m.reduceTasks.idle++
				reduceTask = MasterReduceTask{[]string{}, Task{idle, 0, Worker_{}}}
			}
			reduceTask.filenames = append(reduceTask.filenames, filenames...)
			m.reduceTasks.tasks[taskID] = reduceTask
			log.Printf("Collected files for reduce ID %v: %v\n", taskID, reduceTask.filenames)
		}
		m.workerStatus[report.UUID] = available

		if m.mapTasks.completed == m.mapTasks.total {
			m.reduceTasks.total = m.reduceTasks.idle
			log.Println("All map tasks completed")
			m.cond.Broadcast()
		}

	case Reduce:
		log.Printf("Report for reduce task ID %v\n", report.TaskID)
		reduceTask := m.reduceTasks.tasks[report.TaskID]
		reduceTask.status = completed
		m.reduceTasks.tasks[report.TaskID] = reduceTask
		m.reduceTasks.completed++
		log.Printf("Completed %v/%v reduce tasks\n", m.reduceTasks.completed, m.reduceTasks.total)
		m.workerStatus[report.UUID] = available

		if m.reduceTasks.completed == m.reduceTasks.total {
			log.Println("All reduce tasks completed")
			m.cond.Broadcast()
		}
	}
	return nil
}

//
// check worker health
//
func (m *Master) checkWorkerHealth() {
	for {
		m.mu.Lock()
		if m.mapTasks.completed != m.mapTasks.total {
			for taskID, task := range m.mapTasks.tasks {
				if task.status == idle || task.status == completed {
					continue
				}
				now := time.Now().Unix()
				if now-task.startedTimestamp > deadWorkerThresholdSeconds {
					log.Printf("Worker %v declared dead, reset map task %v\n",
						task.worker.UUID, taskID)
					m.workerStatus[task.worker.UUID] = dead
					task.status = idle
					task.startedTimestamp = 0
					m.mapTasks.tasks[taskID] = task
					m.mapTasks.idle++
					m.taskReseted = true
				}
			}
		} else {
			for taskID, task := range m.reduceTasks.tasks {
				if task.status == idle || task.status == completed {
					continue
				}
				now := time.Now().Unix()
				if now-task.startedTimestamp > deadWorkerThresholdSeconds {
					log.Printf("Worker %v declared dead, reset reduce task %v\n",
						task.worker.UUID, taskID)
					m.workerStatus[task.worker.UUID] = dead
					task.status = idle
					task.startedTimestamp = 0
					m.reduceTasks.tasks[taskID] = task
					m.reduceTasks.idle++
					m.taskReseted = true
				}
			}
		}
		if m.taskReseted {
			m.cond.Broadcast()
		}
		m.mu.Unlock()
		time.Sleep(time.Second)
	}
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
	m.mu.Lock()
	defer m.mu.Unlock()
	return m.reduceTasks.completed == m.reduceTasks.total
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
	// reduceTasks.total could change later due to input, finalize after all map tasks complete
	m.reduceTasks.total = nReduce
	log.Printf("%v reduce tasks specified\n", m.reduceTasks.total)
	go m.checkWorkerHealth()

	m.server()
	return &m
}
