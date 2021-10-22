/*
 * mm.c - Segregated fit memory allocator.
 *     Applied policies:
 *     - Coalesce blocks immediately upon freeing
 *     - Best fit search
 *     - New free blocks are taken from the `wilderness`, which marks the
 *       beginning of unsed heap area.
 *     Data structure:
 *     - Prologue block with pointers to segregated lists
 *     - For each block, there is a header with size, allocated bit, and
 *       predecessor block allocated bit; and two pointers to previous and next
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
#define CHECK_HEAP()

typedef size_t word;
typedef word *wordptr;
typedef unsigned char byte;

/* global control variables */
static wordptr prologue; /* entrance to free lists */
/* `wilderness` points at epilogue block and marks the beginning of unused
 * heap memory.
 */
static wordptr wilderness;

#define SBRK_SIZE (8*WORD_SIZE) /* minimum requested size from sbrk */

/* single word (4) or double word (8) alignment */
#define ALIGNMENT 8

/* round up to the nearest multiple of ALIGNMENT */
#define ALIGN(size) (((size) + (ALIGNMENT-1)) & ~0x7)

#define WORD_SIZE (ALIGN(sizeof(wordptr)))
#define PTR_SIZE (ALIGN(sizeof(wordptr)))

/* free lists */
#define NUM_FREELISTS 7
/* free lists' sizes (excluding header) */
#define FL1_MAXSIZE (1*WORD_SIZE)
#define FL2_MAXSIZE (2*WORD_SIZE)
#define FL3_MAXSIZE (4*WORD_SIZE)
#define FL4_MAXSIZE (8*WORD_SIZE)
#define FL5_MAXSIZE (16*WORD_SIZE)
#define FL6_MAXSIZE (32*WORD_SIZE)
#define FL7_MAXSIZE (64*WORD_SIZE) /* and more */
#define FL1_SIZE (WORD_SIZE + FL1_MAXSIZE)
#define FL2_SIZE (WORD_SIZE + FL2_MAXSIZE)
#define FL3_SIZE (WORD_SIZE + FL3_MAXSIZE)
#define FL4_SIZE (WORD_SIZE + FL4_MAXSIZE)
#define FL5_SIZE (WORD_SIZE + FL5_MAXSIZE)
#define FL6_SIZE (WORD_SIZE + FL6_MAXSIZE)
#define FL7_SIZE (WORD_SIZE + FL7_MAXSIZE)
/* min block size is 2 WORD_SIZE for HDR and FTR, 2 PTR_SIZE for prev and next,
 * and 1 WORD_SIZE payload
 */
#define MIN_BLOCK_SIZE (2*WORD_SIZE + 2*PTR_SIZE + WORD_SIZE)

/* header & footer */
#define ALLOCATED 0x1
#define PRED_ALLOCATED 0x2

/*
 * initial size for a prologue block that has pointers to free lists and an
 * epilogue header
 */
#define INIT_SIZE (NUM_FREELISTS*WORD_SIZE + WORD_SIZE)

/* macro functions */
#define FL_HEAD(i) (*(wordptr *)(prologue + (i)))
#define PAYLOAD(p) ((p) + 1)
#define BLOCK_SIZE(p) (*(p) & ~0x7)
#define SUCC_BLOCK(p) ((wordptr)((byte *)(p) + BLOCK_SIZE(p)))
#define PRED_FTR(p) ((wordptr)((byte *)p - WORD_SIZE))
#define PRED_HDR(p) ((wordptr)((byte *)(PRED_FTR(p)) - \
                     BLOCK_SIZE(PRED_FTR(p)) + WORD_SIZE))
#define PREV_BLOCK(p) (*(wordptr *)((p) + 1))
#define NEXT_BLOCK(p) (*(wordptr *)((p) + 2))
#define HDR(p) (*(p))
#define FTR(p) (*(wordptr)((byte *)SUCC_BLOCK(p) - WORD_SIZE))
#define MAX(x, y) ((x) > (y) ? (x) : (y))

/* function prototypes */
static inline size_t get_freelist_size(int freelist_i);
static inline int find_freelist_i(size_t size);
static wordptr find_best_fit(wordptr head_block, size_t size);
static wordptr extend_freelist(int freelist_i, size_t size);
static void split_block(wordptr p, size_t size);
static inline void insert_freelist(wordptr p, int freelist_i);
static void wire_prevnext(wordptr p);
static void check_heap();

/*
 * mm_init - Initialize the malloc package.
 *     Segregated free lists are allocated.
 */
int mm_init(void)
{
    int i;
    wordptr epilogue;

    if ((prologue = mem_sbrk(MAX(SBRK_SIZE, INIT_SIZE))) == (wordptr)-1)
        return -1;
    for (i = 0; i < NUM_FREELISTS; i++)
        *(wordptr *)(prologue + i) = NULL;
    /* set epilogue */
    epilogue = (wordptr)((byte *)prologue + INIT_SIZE - WORD_SIZE);
    HDR(epilogue) = ALLOCATED | PRED_ALLOCATED;
    /* wilderness points to epilogue */
    wilderness = epilogue;
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
    size = MAX(size, MIN_BLOCK_SIZE);
    /* get free block */
    freelist_i = find_freelist_i(size);
    freelist_sz = get_freelist_size(freelist_i);
    if ((p = find_best_fit(FL_HEAD(freelist_i), size)) == NULL &&
            (p = extend_freelist(freelist_i, MAX(freelist_sz, size))) == NULL)
        return NULL;

    HDR(p) |= ALLOCATED;
    HDR(SUCC_BLOCK(p)) |= PRED_ALLOCATED;
    if (PREV_BLOCK(p) != NULL)
        NEXT_BLOCK(PREV_BLOCK(p)) = NEXT_BLOCK(p);
    else /* first block of list */
        FL_HEAD(freelist_i) = NEXT_BLOCK(p);
    if (NEXT_BLOCK(p) != NULL)
        PREV_BLOCK(NEXT_BLOCK(p)) = PREV_BLOCK(p);
    CHECK_HEAP();
    /* split block */
    if (BLOCK_SIZE(p) >= ALIGN(size) + WORD_SIZE + MIN_BLOCK_SIZE)
        split_block(p, size);
    CHECK_HEAP();
    return PAYLOAD(p);
}

/*
 * mm_free - Free a block. Coalesce immediately if possible.
 */
void mm_free(void *ptr)
{
    wordptr p = (wordptr)ptr - 1, succ_block = SUCC_BLOCK(p), pred_block;
    size_t coalesced_size = BLOCK_SIZE(p),
           pred_allocated_status = HDR(p) & PRED_ALLOCATED;
    int freelist_i;

    /* coalesce */
    /* succ block is free */
    if (!(HDR(succ_block) & ALLOCATED)) {
        wire_prevnext(succ_block);
        coalesced_size += BLOCK_SIZE(succ_block);
    }
    /* pred block is free */
    if (!(HDR(p) & PRED_ALLOCATED)) {
        pred_block = PRED_HDR(p);
        wire_prevnext(pred_block);
        coalesced_size += BLOCK_SIZE(pred_block);
        pred_allocated_status = HDR(pred_block) & PRED_ALLOCATED;
        p = pred_block;
    }

    freelist_i = find_freelist_i(coalesced_size);
    HDR(p) = coalesced_size | pred_allocated_status;
    FTR(p) = HDR(p);
    insert_freelist(p, freelist_i);
    HDR(SUCC_BLOCK(p)) &= ~PRED_ALLOCATED;
    CHECK_HEAP();
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
    size = ALIGN(size);

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
    wordptr curr_block, best_block = NULL;

    for (curr_block = head_block; curr_block != NULL;
            curr_block = NEXT_BLOCK(curr_block)) {
        if (BLOCK_SIZE(curr_block) >= ALIGN(size) + WORD_SIZE) {
            if (best_block == NULL) {
                best_block = curr_block;
                continue;
            }
            if (BLOCK_SIZE(curr_block) < BLOCK_SIZE(best_block))
                best_block = curr_block;
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
    /* extend_size equals requested size plus 1 WORD_SIZE for header plus
     * 1 WORD_SIZE for new epilogue block.
     */
    size_t extend_size = ALIGN(size) + 2*WORD_SIZE;
    size_t pred_allocated_status = HDR(p) & PRED_ALLOCATED;

    if (((byte *)p + extend_size > (byte *)mem_heap_hi) &&
            mem_sbrk(MAX(SBRK_SIZE, extend_size)) == (void *)-1)
        return NULL;
    /* taking whole extend_size as block will get split soon */
    HDR(p) = (extend_size - WORD_SIZE) | pred_allocated_status;
    FTR(p) = HDR(p);
    insert_freelist(p, freelist_i);
    wilderness = SUCC_BLOCK(p);
    HDR(wilderness) = ALLOCATED; /* set epilogue */
    return p;
}

static void split_block(wordptr p, size_t size)
{
    size_t new_block_size = BLOCK_SIZE(p) - (ALIGN(size) + WORD_SIZE),
           pred_allocated_status = HDR(p) & PRED_ALLOCATED;
    wordptr new_block;
    int freelist_i;

    new_block = (wordptr)((byte *)p + ALIGN(size) + WORD_SIZE);
    freelist_i = find_freelist_i(new_block_size);
    HDR(new_block) = new_block_size | PRED_ALLOCATED;
    FTR(new_block) = HDR(new_block);
    insert_freelist(new_block, freelist_i);
    HDR(p) = (ALIGN(size) + WORD_SIZE) | ALLOCATED | pred_allocated_status;
}

static inline void insert_freelist(wordptr p, int freelist_i)
{
    PREV_BLOCK(p) = NULL;
    NEXT_BLOCK(p) = FL_HEAD(freelist_i);
    if (NEXT_BLOCK(p) != NULL)
        PREV_BLOCK(NEXT_BLOCK(p)) = p; /* set next block's previous */
    FL_HEAD(freelist_i) = p; /* set head at prologue */
}

static void wire_prevnext(wordptr p)
{
    int freelist_i = find_freelist_i(BLOCK_SIZE(p));

    if (PREV_BLOCK(p) != NULL)
        NEXT_BLOCK(PREV_BLOCK(p)) = NEXT_BLOCK(p);
    else
        FL_HEAD(freelist_i) = NEXT_BLOCK(p);
    if (NEXT_BLOCK(p) != NULL)
        PREV_BLOCK(NEXT_BLOCK(p)) = PREV_BLOCK(p);
}

static void check_heap()
{
    int i;
    size_t size;
    wordptr p, p_prev;

    /* check: first block after prologue is always PRED_ALLOCATED */
    assert(HDR((byte *)prologue + INIT_SIZE - WORD_SIZE) & PRED_ALLOCATED);
    for (i = 0; i < NUM_FREELISTS; i++) {
        for (p = FL_HEAD(i), p_prev = NULL; p != NULL;
                p_prev = p, p = NEXT_BLOCK(p)) {
            size = BLOCK_SIZE(p);
            /* check: size must be at least MIN_BLOCK_SIZE */
            assert(size >= MIN_BLOCK_SIZE);
            /* check: block belongs to correct list */
            assert(find_freelist_i(size) == i);
            /* check: block is linked properly */
            assert(PREV_BLOCK(p) == p_prev);
            /* check: no two consecutive free blocks */
            // if (!(HDR(p) & ALLOCATED))
            //     assert((HDR(p) & PRED_ALLOCATED) ||
            //            (HDR(SUCC_BLOCK(p)) & ALLOCATED));
            if (HDR(p) & ALLOCATED)
                assert(HDR(SUCC_BLOCK(p)) & PRED_ALLOCATED);
        }
    }
    /* check: successive blocks end at epilogue */
    for (p = (wordptr)((byte *)prologue + INIT_SIZE - WORD_SIZE);
            *p != ALLOCATED && *p != (ALLOCATED | PRED_ALLOCATED);
            p = SUCC_BLOCK(p))
        /* check: if a block is ALLOCATED then succ block is
         * PRED_ALLOCATED
         */
        if (HDR(p) & ALLOCATED)
            assert(HDR(SUCC_BLOCK(p)) & PRED_ALLOCATED);
        /* check: wilderness points to epilogue block */
        assert(BLOCK_SIZE(wilderness) == 0 &&
               (*wilderness == ALLOCATED ||
                *wilderness == (ALLOCATED | PRED_ALLOCATED)));
}
