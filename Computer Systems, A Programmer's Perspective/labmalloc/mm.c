/*
 * mm.c - Segregated fit memory allocator.
 *     Applied policies:
 *     - Coalesce blocks immediately upon freeing
 *     - Best fit search
 *     Data structure:
 *     - Prologue block with pointers to segregated lists
 *     - For each block, there is a header with size, allocated bit, and
 *       previous block allocated bit; and two pointers to previous and next
 *       block in the corresponding free list.
 */
#include <assert.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>

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

/* debug */
#define CHECK_HEAP() check_heap()

typedef size_t word;
typedef word *wordptr;
typedef unsigned char byte;

/* global control variables */
static wordptr prologue; /* entrance to free lists */
/* `wilderness` points at epilogue block and marks the beginning of unused
 * heap memory.
 */
static wordptr wilderness;

#define SBRK_SIZE (64 * (1 << 12)) /* minimum requested size from sbrk */

/* single word (4) or double word (8) alignment */
#define ALIGNMENT 8

/* round up to the nearest multiple of ALIGNMENT */
#define ALIGN(size) (((size) + (ALIGNMENT-1)) & ~0x7)

#define WORD_SIZE (ALIGN(sizeof(word)))
#define PTR_SIZE (ALIGN(sizeof(wordptr)))

/* free lists */
#define NUM_FREELISTS 7
/* free lists' sizes */
#define FL1_MAXSIZE 16*WORD_SIZE
#define FL2_MAXSIZE 32*WORD_SIZE
#define FL3_MAXSIZE 64*WORD_SIZE
#define FL4_MAXSIZE 128*WORD_SIZE
#define FL5_MAXSIZE 256*WORD_SIZE
#define FL6_MAXSIZE 512*WORD_SIZE
#define FL7_MAXSIZE 1024*WORD_SIZE /* and more */
#define FL1_SIZE ALIGN(WORD_SIZE + 2*PTR_SIZE + FL1_MAXSIZE)
#define FL2_SIZE ALIGN(WORD_SIZE + 2*PTR_SIZE + FL2_MAXSIZE)
#define FL3_SIZE ALIGN(WORD_SIZE + 2*PTR_SIZE + FL3_MAXSIZE)
#define FL4_SIZE ALIGN(WORD_SIZE + 2*PTR_SIZE + FL4_MAXSIZE)
#define FL5_SIZE ALIGN(WORD_SIZE + 2*PTR_SIZE + FL5_MAXSIZE)
#define FL6_SIZE ALIGN(WORD_SIZE + 2*PTR_SIZE + FL6_MAXSIZE)
#define FL7_SIZE ALIGN(WORD_SIZE + 2*PTR_SIZE + FL7_MAXSIZE)

/* header & footer */
#define ALLOCATED 0x1
#define PREV_ALLOCATED 0x2

/*
 * initial size for a prologue block that has pointers to free lists and an
 * epilogue header
 */
#define INIT_SIZE ALIGN(NUM_FREELISTS*WORD_SIZE + WORD_SIZE)

/* macro functions */
#define FL_HEAD(i) (*(wordptr *)(prologue + (i)))
#define PAYLOAD(p) ((p) + 3)
#define BLK_SIZE(p) (*(p) & ~0x7)
#define SUCC_BLK(p) ((wordptr)((byte *)(p) + BLK_SIZE(p)))
#define PREV_PTR(p) ((wordptr *)(p + 1))
#define NEXT_PTR(p) ((wordptr *)(p + 2))
#define PREV_BLK(p) (*(wordptr *)(p + 1))
#define NEXT_BLK(p) (*(wordptr *)(p + 2))
#define HDR(p) (*(p))
#define FTR(p) (*(wordptr)((byte *)SUCC_BLK(p) - WORD_SIZE))
#define MAX(x, y) ((x) > (y) ? (x) : (y))

/* function prototypes */
static inline size_t get_freelist_size(int freelist_i);
static inline int find_freelist_i(size_t size);
static wordptr find_best_fit(wordptr head_block, size_t size);
static wordptr extend_freelist(int freelist_i, size_t size);
static void check_heap();

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
    /* wilderness points to epilogue */
    wilderness = (wordptr)((byte *)prologue + INIT_SIZE - WORD_SIZE);
    CHECK_HEAP();
    return 0;
}

/*
 * mm_malloc - Allocate a block by incrementing the brk pointer.
 *     Always allocate a block whose size is a multiple of the alignment.
 */
void *mm_malloc(size_t size)
{
    int freelist_i;
    size_t freelist_sz;
    wordptr p;

    if (size == 0)
        return NULL;
    /* get free block */
    freelist_i = find_freelist_i(size);
    freelist_sz = get_freelist_size(freelist_i);
    if ((p = find_best_fit(FL_HEAD(freelist_i), size)) == NULL &&
            (p = extend_freelist(freelist_i, MAX(freelist_sz, size))) == NULL)
        return NULL;
    /* prepare block */
    *p |= ALLOCATED;
    *(SUCC_BLK(p)) |= PREV_ALLOCATED;
    if (PREV_BLK(p) != NULL)
        *NEXT_PTR(PREV_BLK(p)) = NEXT_BLK(p);
    else /* first block of list */
        FL_HEAD(freelist_i) = NEXT_BLK(p);
    if (NEXT_BLK(p) != NULL)
        *PREV_PTR(NEXT_BLK(p)) = PREV_BLK(p);
    /* split block */
    CHECK_HEAP();
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

/*
 * get_freelist_size - Return minimum size of a freelist for extension.
 */
static inline size_t get_freelist_size(int freelist_i)
{
    size_t freelist_size[NUM_FREELISTS] = {
        FL1_SIZE, FL2_SIZE, FL3_SIZE, FL4_SIZE, FL5_SIZE, FL6_SIZE, FL7_SIZE};

    return freelist_size[freelist_i];
}

/*
 * find_freelist_i - Return index of a segregated free list for a given size.
 */
static inline int find_freelist_i(size_t size)
{
    if (size <= FL1_MAXSIZE) return 0;
    if (size <= FL2_MAXSIZE) return 1;
    if (size <= FL3_MAXSIZE) return 2;
    if (size <= FL4_MAXSIZE) return 3;
    if (size <= FL5_MAXSIZE) return 4;
    if (size <= FL6_MAXSIZE) return 5;
    return 6;
}

/*
 * find_best_fit - Simple linear search entire free list for a best fit
 *     block.
 */
static wordptr find_best_fit(wordptr head_block, size_t size)
{
    wordptr best_block, curr_block;
    size_t best_fit_size, next_block_size;

    if (head_block == NULL)
        return NULL;
    best_block = curr_block = head_block;
    best_fit_size = BLK_SIZE(best_block);
    while ((curr_block = NEXT_BLK(curr_block)) != NULL) {
        next_block_size = BLK_SIZE(curr_block);
        if (next_block_size >= size && next_block_size < best_fit_size) {
            best_block = curr_block;
            best_fit_size = next_block_size;
        }
    }
    return best_block;
}

/*
 * extend_freelist - Extend a freelist when there are no suitable free blocks.
 *     A new block is taken from `wilderness`. If `wilderness` is too small,
 *     call sbrk to extend heap.
 */
static wordptr extend_freelist(int freelist_i, size_t size)
{
    wordptr p = wilderness;
    /* extend_size equals to requested size and 1 WORD_SIZE for header,
     * 2*PTR_SIZE for next and prev pointers, and 1 WORD_SIZE for new epilogue
     * block.
     */
    size_t extend_size = ALIGN(size + 2*WORD_SIZE + 2*PTR_SIZE);
    size_t prev_blk_allocated = *p & PREV_ALLOCATED;

    if (((byte *)p + extend_size > (byte *)mem_heap_hi) &&
            mem_sbrk(MAX(SBRK_SIZE, extend_size)) == (void *)-1)
        return NULL;
    /* taking whole extend_size as block will get split soon */
    HDR(p) = extend_size | prev_blk_allocated;
    FTR(p) = HDR(p);
    PREV_BLK(p) = NULL;
    NEXT_BLK(p) = FL_HEAD(freelist_i);
    if (NEXT_BLK(p) != NULL)
        *PREV_PTR(NEXT_BLK(p)) = p; /* set next block's previous */
    FL_HEAD(freelist_i) = p; /* set head at prologue */
    *((byte *)p + extend_size - WORD_SIZE) = ALLOCATED; /* set epilogue */
    wilderness = (wordptr)((byte *)wilderness + extend_size - WORD_SIZE);
    return p;
}

static void check_heap()
{
    int i;
    size_t size;
    wordptr p, p_prev;

    for (i = 0; i < NUM_FREELISTS; i++) {
        for (p = FL_HEAD(i), p_prev = NULL; p != NULL;
                p = NEXT_BLK(p), p_prev = p) {
            size = BLK_SIZE(p);
            /* check: block belongs to correct list */
            assert(find_freelist_i(size) == i);
            /* check: block is linked properly */
            assert(PREV_BLK(p) == p_prev);
            /* check: no two consecutive free blocks */
            assert(HDR(p) & PREV_ALLOCATED);
        }
    }
}
