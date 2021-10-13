/*
 * mm-naive.c - The fastest, least memory-efficient malloc package.
 *
 * In this naive approach, a block is allocated by simply incrementing
 * the brk pointer.  A block is pure payload. There are no headers or
 * footers.  Blocks are never coalesced or reused. Realloc is
 * implemented directly using mm_malloc and mm_free.
 *
 * NOTE TO STUDENTS: Replace this header comment with your own header
 * comment that gives a high level description of your solution.
 */
#include <stdio.h>
#include <stdlib.h>
#include <assert.h>
#include <unistd.h>
#include <string.h>

#include "mm.h"
#include "memlib.h"

/*********************************************************
 * NOTE TO STUDENTS: Before you do anything else, please
 * provide your team information in the following struct.
 ********************************************************/
team_t team = {
    /* Team name */
    "ateam",
    /* First member's full name */
    "Cuong Dang",
    /* First member's email address */
    "cuongd@pm.me",
    /* Second member's full name (leave blank if none) */
    "",
    /* Second member's email address (leave blank if none) */
    ""
};

typedef size_t *hptr;
typedef unsigned char byte;

/* single word (4) or double word (8) alignment */
#define ALIGNMENT 8

/* rounds up to the nearest multiple of ALIGNMENT */
#define ALIGN(size) (((size) + (ALIGNMENT-1)) & ~0x7)

#define SIZE_T_SIZE (ALIGN(sizeof(size_t)))

/* free lists */
#define NUM_FREELISTS 7
#define FL1_MAXSIZE 2
#define FL2_MAXSIZE 4
#define FL3_MAXSIZE 8
#define FL4_MAXSIZE 16
#define FL5_MAXSIZE 32
#define FL6_MAXSIZE 64
#define FL7_MAXSIZE 128 /* and infinity */
#define FL1_SIZE ALIGN(2*SIZE_T_SIZE + FL1_MAXSIZE)
#define FL2_SIZE ALIGN(2*SIZE_T_SIZE + FL2_MAXSIZE)
#define FL3_SIZE ALIGN(2*SIZE_T_SIZE + FL3_MAXSIZE)
#define FL4_SIZE ALIGN(2*SIZE_T_SIZE + FL4_MAXSIZE)
#define FL5_SIZE ALIGN(2*SIZE_T_SIZE + FL5_MAXSIZE)
#define FL6_SIZE ALIGN(2*SIZE_T_SIZE + FL6_MAXSIZE)
#define FL7_SIZE ALIGN(2*SIZE_T_SIZE + FL7_MAXSIZE)

/* header & footer */
#define ALLOCATED 0x1
#define PREV_ALLOCATED 0x2

/*
 * Initial size for a prologue block that has pointers to different size free
 * lists, an epilogue header, and one free block for each free list.
 */
#define INIT_SIZE ALIGN(NUM_FREELISTS*sizeof(hptr) + SIZE_T_SIZE + \
        FL1_SIZE + FL2_SIZE + FL3_SIZE + FL4_SIZE + FL5_SIZE + FL6_SIZE + \
        FL7_SIZE)

/* macro functions */
#define BLK_SIZE(p) (*(size_t *)(p) & ~0x7)
#define NEXT_BLK(p) ((hptr)((byte *)(p) + BLK_SIZE(p)))
#define FTR(p) ((hptr)((byte *)NEXT_BLK(p) - SIZE_T_SIZE))

/* function prototypes */
void init_freelist(int i, int minsize);

static hptr prologue;

/*
 * mm_init - initialize the malloc package.
 */
int mm_init(void)
{
    int i;
    int freelist_size[NUM_FREELISTS] = {FL1_SIZE, FL2_SIZE, FL3_SIZE, FL4_SIZE,
            FL5_SIZE, FL6_SIZE, FL7_SIZE};

    if ((prologue = mem_sbrk(INIT_SIZE)) == (hptr)-1) {
        return -1;
    }
    for (i = 0; i < NUM_FREELISTS; i++) {
        init_freelist(i, freelist_size[i]);
    }
    return 0;
}

/*
 * mm_malloc - Allocate a block by incrementing the brk pointer.
 *     Always allocate a block whose size is a multiple of the alignment.
 */
void *mm_malloc(size_t size)
{
    int newsize = ALIGN(size + SIZE_T_SIZE);
    void *p = mem_sbrk(newsize);
    if (p == (void *)-1)
	return NULL;
    else {
        *(size_t *)p = size;
        return (void *)((char *)p + SIZE_T_SIZE);
    }
}

/*
 * mm_free - Freeing a block does nothing.
 */
void mm_free(void *ptr)
{
}

/*
 * mm_realloc - Implemented simply in terms of mm_malloc and mm_free
 */
void *mm_realloc(void *ptr, size_t size)
{
    void *oldptr = ptr;
    void *newptr;
    size_t copySize;

    newptr = mm_malloc(size);
    if (newptr == NULL)
      return NULL;
    copySize = *(size_t *)((char *)oldptr - SIZE_T_SIZE);
    if (size < copySize)
      copySize = size;
    memcpy(newptr, oldptr, copySize);
    mm_free(oldptr);
    return newptr;
}

void init_freelist(int freelist_i, int size)
{
    hptr p = prologue + NUM_FREELISTS;
    int i;

    for (i = 0; i < freelist_i; i++) {
        p = NEXT_BLK(p);
    }
    *p = size | ALLOCATED | PREV_ALLOCATED;
    *(hptr *)(p + 1) = NULL;
    *(FTR(p)) = *p;
    *(hptr *)(prologue + i) = p;
}
