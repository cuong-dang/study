#include <getopt.h>
#include <math.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>

#include "cachelab.h"

#define ADDRESS_SZ 64
#define OPTSTRING "s:E:b:t:"
#define TRACELINE_FMT " %c %llx,%d"
#define ERR_MSG_MALLOC "Fail to malloc"
#define ERR_MSG_OPTARG "Invalid args"
#define ERR_MSG_OPENFILE "Could not open trace file"


/* Declaration */
/* Cache line */
typedef unsigned long long address;

/**
 * Represent a cache line with timestamp (for LRU cache replacement policy).
 */
struct line {
    int valid;
    address tag;
    int timestamp;
};

/* Cache */
/** Represent a cache. */
struct cache {
    struct line **lines;
    int s;
    int E;
    int b;
    int misses;
    int hits;
    int evicts;
    int clock;
};

/**
 * Make a new cache given number of set index bits `s`, associativity `E`, and
 * block bits `b`.
 *
 * Return a pointer to the created cache.
 */
struct cache *cache_make(int s, int E, int b);

/**
 * Make new sets of cache lines given number of set `S` and associativity `E`.
 *
 * Return a pointer to the created sets.
 */
struct line **make_lines(int S, int E);

/**
 * Simulate cache behavior to get a number of misses and hits given a trace
 * file. Assume the trace file is well-formed.
 */
void cache_sim(struct cache *cache, FILE *tracefile);

/**
 * Given an address, simulate whether it will be a hit or miss to update
 * cache's internal state.
 */
void cache_process(struct cache *cache, address addr);

/**
 * Return the corresponding cache line of a given address or NULL if such line
 * does not exist or is invalid.
 */
struct line *cache_get_line(struct cache *cache, address addr);

/** Return the cache set index of a given address. */
int cache_get_set(struct cache *cache, address addr);

/** Return the cache tag of a given address. */
address cache_get_tag(struct cache *cache, address addr);

/** Simulate inserting an address into cache. */
void cache_insert(struct cache *cache, address addr);

/** Clean up. */
void cache_free(struct cache *cache);


/* Helper */
/** Print out `msg` and quit. */
void quit(char *msg);
/** Convert a string to int. Quit if invalid input. */
int read_int(char *s);


/* Implementation */
struct cache *cache_make(int s, int E, int b) {
    struct cache *cache;

    if (!(cache = malloc(sizeof(struct cache))))
        quit(ERR_MSG_MALLOC);
    cache->s = s;
    cache->E = E;
    cache->b = b;
    cache->lines = make_lines(pow(2, cache->s), cache->E);
    cache->misses = 0;
    cache->hits = 0;
    cache->evicts = 0;
    cache->clock = 0;
    return cache;
}

struct line **make_lines(int S, int E) {
    struct line **lines;
    int i;

    if (!(lines = malloc(sizeof(struct line *) * S * E)))
        quit(ERR_MSG_MALLOC);
    for (i = 0; i < S * E; i++) {
        if (!(lines[i] = malloc(sizeof(struct line))))
            quit(ERR_MSG_MALLOC);
        lines[i]->valid = 0;
        lines[i]->timestamp = 0;
    }
    return lines;
}

void cache_sim(struct cache *cache, FILE *tracefile) {
    char op;
    address addr;
    int size;

    while ((fscanf(tracefile, TRACELINE_FMT, &op, &addr, &size)) != EOF) {
        switch (op) {
        case 'M':
            cache_process(cache, addr);
            cache_process(cache, addr);
            break;
        case 'L': case 'S':
            cache_process(cache, addr);
            break;
        default:
            break;
        }
    }
}

void cache_process(struct cache *cache, address addr) {
    struct line *line;

    cache->clock++;
    line = cache_get_line(cache, addr);
    if (!line) {
        cache->misses++;
        cache_insert(cache, addr);
    } else {
        line->timestamp = cache->clock;
        cache->hits++;
    }
    return;
}

struct line *cache_get_line(struct cache *cache, address addr) {
    int set_index = cache_get_set(cache, addr);
    address addr_tag = cache_get_tag(cache, addr), line_tag;
    struct line **set_lines = cache->lines+(set_index * cache->E);
    int i;

    for (i = 0; i < cache->E; i++) {
        line_tag = set_lines[i]->tag;
        if (set_lines[i]->valid && line_tag == addr_tag)
            return set_lines[i];
    }
    return NULL;
}

int cache_get_set(struct cache *cache, address addr) {
    return
        ((addr >> cache->b) <<
         (ADDRESS_SZ - cache->s)) >>
        (ADDRESS_SZ - cache->s);
}

address cache_get_tag(struct cache *cache, address addr) {
    return addr >> (cache->b + cache->s);
}

void cache_insert(struct cache *cache, address addr) {
    int set_index = cache_get_set(cache, addr);
    address addr_tag = cache_get_tag(cache, addr);
    struct line **set_lines = cache->lines+(set_index * cache->E);
    struct line *lru_line = NULL;
    int i;

    /* Try inserting into an empty line. */
    for (i = 0; i < cache->E; i++) {
        if (!set_lines[i]->valid) {
            set_lines[i]->valid = 1;
            set_lines[i]->tag = addr_tag;
            set_lines[i]->timestamp = cache->clock;
            return;
        } else if (!lru_line) {
            lru_line = set_lines[i];
        } else if (set_lines[i]->timestamp < lru_line->timestamp) {
            lru_line = set_lines[i];
        }
    }
    /* No empty line found. Evict LRU line. */
    cache->evicts++;
    lru_line->tag = addr_tag;
    lru_line->timestamp = cache->clock;
}

void cache_free(struct cache *cache) {
    int S = pow(2, cache->s), i;

    for (i = 0; i < S * cache->E; i++) {
        free(cache->lines[i]);
    }
    free(cache);
}

void quit(char *msg) {
    printf("%s\n", msg);
    abort();
}

int read_int(char *s) {
    char *errptr;
    int result;

    result = (int) strtol(s, &errptr, 10);
    if (*s == '\0' || *errptr != '\0') {
        quit(ERR_MSG_OPTARG);
    }
    return result;
}


int main(int argc, char **argv) {
    int ch;
    int s, E, b;
    FILE *tracefile;
    struct cache *cache;

    /* Read stdin args. */
    while ((ch = getopt(argc, argv, OPTSTRING)) != -1) {
        switch (ch) {
        case 's':
            s = read_int(optarg);
            break;
        case 'E':
            E = read_int(optarg);
            break;
        case 'b':
            b = read_int(optarg);
            break;
        case 't':
            if (!(tracefile = fopen(optarg, "r")))
                quit(ERR_MSG_OPENFILE);
            break;
        case '?': case ':':
        default:
            quit(ERR_MSG_OPTARG);
        }
    }

    cache = cache_make(s, E, b);
    cache_sim(cache, tracefile);

    fclose(tracefile);
    cache_free(cache);
    printSummary(cache->hits, cache->misses, cache->evicts);
    return 0;
}
