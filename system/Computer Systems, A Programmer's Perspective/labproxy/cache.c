#include <assert.h>
#include "csapp.h"
#include "cache.h"

#define MIN(x, y) ((x) < (y) ? (x) : (y))

static cache_object *lookup(cache_object *node, char *key, int key_length);
static cache_object *insert(cache *c, cache_object *node, char *key,
        int key_length, char *payload, int payload_size, int age);
static int keycmp(char *k1, int k1len, char *k2, int k2len);
static void evict_lru(cache *c);
static void find_lru(cache_object *node, cache_object **lru);

cache *make_cache(int max_size, int max_object_size)
{
    cache *c = (cache *)Malloc(sizeof(cache));

    c->max_size = max_size;
    c->max_object_size = max_object_size;
    c->cached_bytes = 0;
    c->root = NULL;
    return c;
}

cache_object *cache_lookup(cache *c, char *key)
{
    return lookup(c->root, key, strlen(key));
}

void cache_insert(cache *c, char *key, int key_length, char *payload,
        int payload_size, int age)
{
    c->root = insert(c, c->root, key, key_length, payload, payload_size, age);
}

void cache_renew_object(cache_object *op, int age)
{
    op->age = age;
}

static cache_object *lookup(cache_object *node, char *key, int key_length)
{
    int cmp;

    if (node == NULL)
        return NULL;
    if ((cmp = keycmp(node->key, node->key_length, key, key_length)) == 0) {
        if (!node->is_evicted)
            return node;
        return NULL;
    }
    if (cmp < 0)
        return lookup(node->left, key, key_length);
    return lookup(node->right, key, key_length);
}

static cache_object *insert(cache *c, cache_object *node, char *key,
        int key_length, char *payload, int payload_size, int age)
{
    int cmp, new_cache_size;

    if (node == NULL) {
        while (c->cached_bytes + payload_size > c->max_size)
            evict_lru(c);
        c->cached_bytes += payload_size;

        node = (cache_object *)Malloc(sizeof(cache_object));
        node->age = age;
        node->is_evicted = 0;
        node->key_length = key_length;
        node->key = (char *)Malloc(key_length + 1);
        strcpy(node->key, key);
        node->payload_size = payload_size;
        node->payload = (char *)Malloc(payload_size);
        memcpy(node->payload, payload, payload_size);
        node->left = NULL;
        node->right = NULL;
        return node;
    }
    if ((cmp = keycmp(node->key, node->key_length, key, key_length)) == 0) {
        while (1) {
            if (!node->is_evicted)
                new_cache_size = c->cached_bytes - node->payload_size + \
                        payload_size;
            else
                new_cache_size = c->cached_bytes + payload_size;
            if (new_cache_size <= c->max_size)
                break;
            evict_lru(c);
        }
        c->cached_bytes = new_cache_size;
        if (!node->is_evicted)
            free(node->payload);

        node->age = age;
        node->is_evicted = 0;
        node->payload_size = payload_size;
        node->payload = (char *)Malloc(payload_size);
        memcpy(node->payload, payload, payload_size);
        return node;
    }
    if (cmp < 0)
        node->left = insert(c, node->left, key, key_length, payload,
                payload_size, age);
    else
        node->right = insert(c, node->right, key, key_length, payload,
                payload_size, age);
    return node;
}

static int keycmp(char *k1, int k1len, char *k2, int k2len)
{
    int cmp = strncmp(k1, k2, MIN(k1len, k2len));

    if (cmp)
        return cmp;
    return k1len - k2len;
}

static void evict_lru(cache *c)
{
    cache_object *lru;

    assert(c->root != NULL);
    lru = c->root;
    find_lru(c->root, &lru);
    assert(!lru->is_evicted);
    printf("  Evict cache %s\n", lru->key);
    lru->is_evicted = 1;
    c->cached_bytes -= lru->payload_size;
    free(lru->payload);
}

static void find_lru(cache_object *node, cache_object **lru)
{
    if (!node->is_evicted && node->age < (*lru)->age)
        *lru = node;
    if (node->left != NULL) {
        if ((*lru)->is_evicted)
            *lru = node->left;
        find_lru(node->left, lru);
    }
    if (node->right != NULL) {
        if ((*lru)->is_evicted)
            *lru = node->right;
        find_lru(node->right, lru);
    }
}
