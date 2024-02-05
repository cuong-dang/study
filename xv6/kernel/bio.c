// Buffer cache.
//
// The buffer cache is a linked list of buf structures holding
// cached copies of disk block contents.  Caching disk blocks
// in memory reduces the number of disk reads and also provides
// a synchronization point for disk blocks used by multiple processes.
//
// Interface:
// * To get a buffer for a particular disk block, call bread.
// * After changing buffer data, call bwrite to write it to disk.
// * When done with the buffer, call brelse.
// * Do not use the buffer after calling brelse.
// * Only one process at a time can use a buffer,
//     so do not keep them longer than necessary.
#include "types.h"
#include "param.h"
#include "spinlock.h"
#include "sleeplock.h"
#include "riscv.h"
#include "defs.h"
#include "fs.h"
#include "buf.h"

#define NBUCKET 13

struct {
  struct spinlock lock;
  struct spinlock locks[NBUCKET];

  struct buf buf[NBUF];
  struct buf buckets[NBUCKET];
} bcache;

void binit(void) {
  int i, h;
  struct buf *b;

  initlock(&bcache.lock, "bcache");
  for (i = 0; i < NBUCKET; i++) {
    initlock(&bcache.locks[i], "bcache");
    bcache.buckets[i].prev = &bcache.buckets[i];
    bcache.buckets[i].next = &bcache.buckets[i];
  }
  for (i = 0; i < NBUF; i++) {
    h = i % NBUCKET;
    b = &bcache.buf[i];

    b->next = bcache.buckets[h].next;
    b->prev = &bcache.buckets[h];
    bcache.buckets[h].next->prev = b;
    bcache.buckets[h].next = b;
  }
}

// Look through buffer cache for block on device dev.
// If not found, allocate a buffer.
// In either case, return locked buffer.
static struct buf *bget(uint dev, uint blockno) {
  struct buf *b;
  int h = blockno % NBUCKET, hh;

  acquire(&bcache.locks[h]);
  // Is the block already cached?
  for (b = bcache.buckets[h].next; b != &bcache.buckets[h]; b = b->next) {
    if (b->dev == dev && b->blockno == blockno) {
      b->refcnt++;
      release(&bcache.locks[h]);
      acquiresleep(&b->lock);
      return b;
    }
  }
  release(&bcache.locks[h]);

  // Not cached, find a block
  acquire(&bcache.lock);
  // Recheck
  acquire(&bcache.locks[h]);
  for (b = bcache.buckets[h].next; b != &bcache.buckets[h]; b = b->next) {
    if (b->dev == dev && b->blockno == blockno) {
      b->refcnt++;
      release(&bcache.lock);
      release(&bcache.locks[h]);
      acquiresleep(&b->lock);
      return b;
    }
  }
  // Still not cached
  for (int i = 0; i < NBUF; i++) {
    b = &bcache.buf[i];
    hh = b->blockno % NBUCKET;

    if (h != hh) {
      acquire(&bcache.locks[hh]);
    }

    if (b->refcnt == 0) {
      b->next->prev = b->prev;
      b->prev->next = b->next;
      b->dev = dev;
      b->blockno = blockno;
      b->valid = 0;
      b->refcnt = 1;

      b->next = bcache.buckets[h].next;
      b->prev = &bcache.buckets[h];
      bcache.buckets[h].next->prev = b;
      bcache.buckets[h].next = b;
      release(&bcache.lock);
      if (h != hh) {
        release(&bcache.locks[hh]);
      }
      release(&bcache.locks[h]);
      acquiresleep(&b->lock);
      return b;
    }

    if (h != hh) {
      release(&bcache.locks[hh]);
    }
  }
  panic("bget: no buffers");
}

// Return a locked buf with the contents of the indicated block.
struct buf *bread(uint dev, uint blockno) {
  struct buf *b;

  b = bget(dev, blockno);
  if (!b->valid) {
    virtio_disk_rw(b, 0);
    b->valid = 1;
  }
  return b;
}

// Write b's contents to disk.  Must be locked.
void bwrite(struct buf *b) {
  if (!holdingsleep(&b->lock))
    panic("bwrite");
  virtio_disk_rw(b, 1);
}

// Release a locked buffer.
// Move to the head of the most-recently-used list.
void brelse(struct buf *b) {
  int h = b->blockno % NBUCKET;

  if (!holdingsleep(&b->lock))
    panic("brelse");

  releasesleep(&b->lock);
  acquire(&bcache.locks[h]);
  b->refcnt--;
  release(&bcache.locks[h]);
}

void bpin(struct buf *b) {
  acquire(&bcache.lock);
  b->refcnt++;
  release(&bcache.lock);
}

void bunpin(struct buf *b) {
  acquire(&bcache.lock);
  b->refcnt--;
  release(&bcache.lock);
}
