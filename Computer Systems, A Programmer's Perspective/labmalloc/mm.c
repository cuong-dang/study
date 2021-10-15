/*
 * mm.c - Segregated fits memory allocator. The high level policies are
 *     as follows.
 *     - Coalesce blocks immediately upon freeing.
 *     - Best fit search.
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

typedef size_t word;
typedef word *wordptr;
typedef unsigned char byte;

/* global control variables */
static wordptr prologue;
static wordptr wilderness;

#define SBRK_SIZE (1 << 31)

/* single word (4) or double word (8) alignment */
#define ALIGNMENT 8

/* round up to the nearest multiple of ALIGNMENT */
#define ALIGN(size) (((size) + (ALIGNMENT-1)) & ~0x7)

#define WORD_SIZE (ALIGN(sizeof(word)))
#define PTR_SIZE (ALIGN(sizeof(wordptr)))

/* free lists */
#define NUM_FREELISTS 7
#define FL1_MAXSIZE 2
#define FL2_MAXSIZE 4
#define FL3_MAXSIZE 8
#define FL4_MAXSIZE 16
#define FL5_MAXSIZE 32
#define FL6_MAXSIZE 64
#define FL7_MAXSIZE 128 /* and infinity */
#define FL1_SIZE ALIGN(WORD_SIZE + 2*PTR_SIZE + FL1_MAXSIZE*WORD_SIZE)
#define FL2_SIZE ALIGN(WORD_SIZE + 2*PTR_SIZE + FL2_MAXSIZE*WORD_SIZE)
#define FL3_SIZE ALIGN(WORD_SIZE + 2*PTR_SIZE + FL3_MAXSIZE*WORD_SIZE)
#define FL4_SIZE ALIGN(WORD_SIZE + 2*PTR_SIZE + FL4_MAXSIZE*WORD_SIZE)
#define FL5_SIZE ALIGN(WORD_SIZE + 2*PTR_SIZE + FL5_MAXSIZE*WORD_SIZE)
#define FL6_SIZE ALIGN(WORD_SIZE + 2*PTR_SIZE + FL6_MAXSIZE*WORD_SIZE)
#define FL7_SIZE ALIGN(WORD_SIZE + 2*PTR_SIZE + FL7_MAXSIZE*WORD_SIZE)

#define FL(i) (prologue + (i))

/* header & footer */
#define ALLOCATED 0x1
#define PREV_ALLOCATED 0x2

/*
 * initial size for a prologue block that has pointers to different size free
 * lists and an epilogue header
 */
#define INIT_SIZE ALIGN(NUM_FREELISTS*sizeof(wordptr) + WORD_SIZE)

/* macro functions */
#define PAYLOAD(p) (p + 3)
#define BLK_SIZE(p) (*(word *)(p) & ~0x7)
#define SUCC_BLK(p) ((wordptr)((byte *)(p) + BLK_SIZE(p)))
#define FTR(p) ((wordptr)((byte *)SUCC_BLK(p) - WORD_SIZE))
#define NEXT_BLK(p) (*(wordptr *)(p + 2))
#define PREV_BLK(p) (*(wordptr *)(p + 1))

/* function prototypes */
static inline size_t get_freelist_size(int freelist_i);
static inline int find_freelist(size_t size);
static wordptr find_best_fit(wordptr first_block, size_t size);
static wordptr extend_freelist(int freelist_i);

/*
 * mm_init - Initialize the malloc package.
 *     Segregated free lists are allocated.
 */
int mm_init(void)
{
    int i;

    if ((prologue = mem_sbrk(SBRK_SIZE)) == (wordptr)-1)
        return -1;
    for (i = 0; i < NUM_FREELISTS; i++)
        *(wordptr *)(prologue + i) = NULL;
    /* set epilogue */
    *(wordptr)((byte *)prologue + INIT_SIZE - WORD_SIZE) = ALLOCATED;
    wilderness = prologue + INIT_SIZE;
    return 0;
}

/*
 * mm_malloc - Allocate a block by incrementing the brk pointer.
 *     Always allocate a block whose size is a multiple of the alignment.
 */
void *mm_malloc(size_t size)
{
    int freelist_i;
    wordptr p;

    if (size == 0)
        return NULL;
    freelist_i = find_freelist(size);
    if ((p = find_best_fit(FL(freelist_i), size)) == NULL ||
            (p = extend_freelist(freelist_i)) == NULL)
        return NULL;
    *p |= ALLOCATED;
    *(SUCC_BLK(p)) |= PREV_ALLOCATED;
    if (p + 1 != NULL)
        *(wordptr *)(PREV_BLK(p) + 2) = p + 2;
    else /* first block of list */
        *(wordptr *)FL(freelist_i) = NEXT_BLK(p);
    if (p + 2 != NULL)
        *(wordptr *)(NEXT_BLK(p) + 1) = PREV_BLK(p);
    /* TODO split block */
    return PAYLOAD(p);
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
    copySize = *(size_t *)((char *)oldptr - WORD_SIZE);
    if (size < copySize)
      copySize = size;
    memcpy(newptr, oldptr, copySize);
    mm_free(oldptr);
    return newptr;
}

static inline size_t get_freelist_size(int freelist_i)
{
    size_t freelist_size[NUM_FREELISTS] = {
        FL1_SIZE, FL2_SIZE, FL3_SIZE, FL4_SIZE, FL5_SIZE, FL6_SIZE, FL7_SIZE};

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

static wordptr find_best_fit(wordptr first_block, size_t size)
{
    wordptr best_block = first_block, next_block = first_block;
    size_t best_fit_size = BLK_SIZE(best_block), next_block_size;

    while ((next_block = NEXT_BLK(next_block)) != NULL) {
        next_block_size = BLK_SIZE(next_block);
        if (next_block_size >= size && next_block_size < best_fit_size) {
            best_block = next_block;
            best_fit_size = next_block_size;
        }
    }
    return next_block;
}

static wordptr extend_freelist(int freelist_i)
{
    size_t extend_size = get_freelist_size(freelist_i);
    wordptr p = wilderness;

    if (((byte *)p + ALIGN(extend_size) > (byte *)mem_heap_hi) ||
            mem_sbrk(SBRK_SIZE) == (void *)-1)
        return NULL;
    *p = ALIGN(extend_size); /* set header */
    *(wordptr *)FTR(p) = p; /* set footer */
    *(wordptr *)(p + 1) = NULL; /* set previous */
    *(wordptr *)(p + 2) = FL(freelist_i); /* set next */
    if (*(wordptr *)(p + 2) != NULL)
        *(wordptr *)(NEXT_BLK(p) + 1) = p; /* set next block's previous */
    *(wordptr *)FL(freelist_i) = p; /* set head at prologue */
    wilderness = (wordptr)((byte *)wilderness + ALIGN(extend_size));
    return p;
}
