#include "types.h"
#include "riscv.h"
#include "defs.h"
#include "date.h"
#include "param.h"
#include "memlayout.h"
#include "spinlock.h"
#include "proc.h"

uint64 sys_exit(void) {
  int n;
  if (argint(0, &n) < 0)
    return -1;
  exit(n);
  return 0; // not reached
}

uint64 sys_getpid(void) { return myproc()->pid; }

uint64 sys_fork(void) { return fork(); }

uint64 sys_wait(void) {
  uint64 p;
  if (argaddr(0, &p) < 0)
    return -1;
  return wait(p);
}

uint64 sys_sbrk(void) {
  int addr;
  int n;

  if (argint(0, &n) < 0)
    return -1;
  addr = myproc()->sz;
  if (growproc(n) < 0)
    return -1;
  return addr;
}

uint64 sys_sleep(void) {
  int n;
  uint ticks0;

  if (argint(0, &n) < 0)
    return -1;
  acquire(&tickslock);
  ticks0 = ticks;
  while (ticks - ticks0 < n) {
    if (myproc()->killed) {
      release(&tickslock);
      return -1;
    }
    sleep(&ticks, &tickslock);
  }
  release(&tickslock);
  backtrace();
  return 0;
}

uint64 sys_kill(void) {
  int pid;

  if (argint(0, &pid) < 0)
    return -1;
  return kill(pid);
}

// return how many clock tick interrupts have occurred
// since start.
uint64 sys_uptime(void) {
  uint xticks;

  acquire(&tickslock);
  xticks = ticks;
  release(&tickslock);
  return xticks;
}

uint64 sys_sigalarm(void) {
  struct proc *p = myproc();
  int ticks;
  uint64 fn;

  if (argint(0, &ticks) < 0 || argaddr(1, &fn) < 0) {
    return -1;
  }
  p->sigalarm_ticks = ticks;
  p->sigalarm_remaining = p->sigalarm_ticks;
  p->sigalarm_handling = 0;
  p->sigalarm_fn = fn;
  return 0;
}

uint64 sys_sigreturn(void) {
  struct proc *p = myproc();

  p->trapframe->ra = p->sigalarm_saved->ra;
  p->trapframe->sp = p->sigalarm_saved->sp;
  p->trapframe->gp = p->sigalarm_saved->gp;
  p->trapframe->tp = p->sigalarm_saved->tp;
  p->trapframe->t0 = p->sigalarm_saved->t0;
  p->trapframe->t1 = p->sigalarm_saved->t1;
  p->trapframe->t2 = p->sigalarm_saved->t2;
  p->trapframe->s0 = p->sigalarm_saved->s0;
  p->trapframe->s1 = p->sigalarm_saved->s1;
  p->trapframe->a0 = p->sigalarm_saved->a0;
  p->trapframe->a1 = p->sigalarm_saved->a1;
  p->trapframe->a2 = p->sigalarm_saved->a2;
  p->trapframe->a3 = p->sigalarm_saved->a3;
  p->trapframe->a4 = p->sigalarm_saved->a4;
  p->trapframe->a5 = p->sigalarm_saved->a5;
  p->trapframe->a6 = p->sigalarm_saved->a6;
  p->trapframe->a7 = p->sigalarm_saved->a7;
  p->trapframe->s2 = p->sigalarm_saved->s2;
  p->trapframe->s3 = p->sigalarm_saved->s3;
  p->trapframe->s4 = p->sigalarm_saved->s4;
  p->trapframe->s5 = p->sigalarm_saved->s5;
  p->trapframe->s6 = p->sigalarm_saved->s6;
  p->trapframe->s7 = p->sigalarm_saved->s7;
  p->trapframe->s8 = p->sigalarm_saved->s8;
  p->trapframe->s9 = p->sigalarm_saved->s9;
  p->trapframe->s10 = p->sigalarm_saved->s10;
  p->trapframe->s11 = p->sigalarm_saved->s11;
  p->trapframe->t3 = p->sigalarm_saved->t3;
  p->trapframe->t4 = p->sigalarm_saved->t4;
  p->trapframe->t5 = p->sigalarm_saved->t5;
  p->trapframe->t6 = p->sigalarm_saved->t6;

  p->trapframe->epc = p->sigalarm_saved->epc;

  p->sigalarm_handling = 0;
  return 0;
}
