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

static hptr prologue;

/* single word (4) or double word (8) alignment */
#define ALIGNMENT 8

/* round up to the nearest multiple of ALIGNMENT */
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

#define FL(i) (prologue + (i))

/* header & footer */
#define ALLOCATED 0x1
#define PREV_ALLOCATED 0x2

/*
 * initial size for a prologue block that has pointers to different size free
 * lists, an epilogue header, and one free block for each free list
 */
#define INIT_SIZE ALIGN(NUM_FREELISTS*sizeof(hptr) + SIZE_T_SIZE + \
        FL1_SIZE + FL2_SIZE + FL3_SIZE + FL4_SIZE + FL5_SIZE + FL6_SIZE + \
        FL7_SIZE)

/* macro functions */
#define BLK_SIZE(p) (*(size_t *)(p) & ~0x7)
#define NEXT_BLK(p) ((hptr)((byte *)(p) + BLK_SIZE(p)))
#define FTR(p) ((hptr)((byte *)NEXT_BLK(p) - SIZE_T_SIZE))

/* function prototypes */
static void init_freelist(int freelist_i);
static inline size_t get_freelist_size(int freelist_i);
static inline int find_freelist(size_t size);
static hptr extend_freelist(int freelist_i);

/*
 * mm_init - Initialize the malloc package.
 *     Segregated free lists are allocated.
 */
int mm_init(void)
{
    int i;

    if ((prologue = mem_sbrk(INIT_SIZE)) == (hptr)-1)
        return -1;
    for (i = 0; i < NUM_FREELISTS; i++)
        init_freelist(i);
    /* set epilogue */
    *(hptr)((byte *)prologue + INIT_SIZE - SIZE_T_SIZE) = ALLOCATED;
    return 0;
}

/*
 * mm_malloc - Allocate a block by incrementing the brk pointer.
 *     Always allocate a block whose size is a multiple of the alignment.
 */
void *mm_malloc(size_t size)
{
    int freelist_i;
    hptr p;

    if (size == 0)
        return NULL;
    freelist_i = find_freelist(size);
    if ((p = find_first_fit(FL(freelist_i), size)) == NULL ||
            (p = extend_freelist(freelist_i)) == NULL)
        return NULL;
    return p + 1;
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

/*
 * init_freelist - Initialize a free list.
 *     Set prologue pointer to the first block. Next block is set to NULL.
 */
static void init_freelist(int freelist_i)
{
    hptr p = prologue + NUM_FREELISTS;
    int i;

    for (i = 0; i < freelist_i; i++)
        p = NEXT_BLK(p);
    *p = get_freelist_size(freelist_i); /* set header */
    *(hptr *)(p + 1) = NULL; /* set next */
    *(FTR(p)) = *p; /* copy header to footer */
    *(hptr *)(prologue + i) = p; /* set free list's pointer in prologue */
}

static inline size_t get_freelist_size(int freelist_i)
{
    size_t freelist_size[NUM_FREELISTS] = {FL1_SIZE, FL2_SIZE, FL3_SIZE,
            FL4_SIZE, FL5_SIZE, FL6_SIZE, FL7_SIZE};

    return freelist_size[freelist_i];
}

/*
 * find_freelist - Return index of a segregated free list for a given size.
 */
static inline int find_freelist(size_t size)
{
    if (size <= FL1_MAXSIZE) return 1;
    if (size <= FL2_MAXSIZE) return 2;
    if (size <= FL3_MAXSIZE) return 3;
    if (size <= FL4_MAXSIZE) return 4;
    if (size <= FL5_MAXSIZE) return 5;
    if (size <= FL6_MAXSIZE) return 6;
    return 7;
}

static hptr extend_freelist(int freelist_i)
{
    size_t extend_size = get_freelist_size(freelist_i);
    hptr p;

    if ((p = mem_sbrk(extend_size)) == (hptr)-1)
        return NULL;
    *(p - 1) = extend_size; /* set header */
    *(hptr *)p = FL(freelist_i); /* set next pointer */
    *(hptr *)FL(freelist_i) = (p - 1); /* set prologue's pointer */
    *(size_t *)((char *)p - SIZE_T_SIZE + extend_size) = ALLOCATED;
    /* TODO coalesce block before epilogue */
}
