#include "csapp.h"
#include "cache.h"

#define MIN(x, y) ((x) < (y) ? (x) : (y))

static cache_object *lookup(cache_object *node, char *key, int key_length);
static cache_object *insert(cache_object *node, char *key, int key_length,
        char *payload, int payload_size);
static int keycmp(char *k1, int k1len, char *k2, int k2len);

cache *make_cache(int max_size, int max_object_size)
{
    cache *c = (cache *)Malloc(sizeof(cache));

    c->max_size = max_size;
    c->max_object_size = max_object_size;
    c->num_objects = 0;
    c->cached_bytes = 0;
    c->root = NULL;
}

cache_object *cache_lookup(cache *c, char *key)
{
    return lookup(c->root, key, strlen(key));
}

void cache_insert(cache *c, char *key, int key_length, char *payload,
        int payload_size)
{
    c->root = insert(c->root, key, key_length, payload, payload_size);
}

void cache_renew_object(cache_object *op)
{
    ;
}

static cache_object *lookup(cache_object *node, char *key, int key_length)
{
    int cmp;

    if (node == NULL)
        return NULL;
    if ((cmp = keycmp(node->key, node->key_length, key, key_length)) == 0)
        return node;
    if (cmp < 0)
        return lookup(node->left, key, key_length);
    return lookup(node->right, key, key_length);
}

static cache_object *insert(cache_object *node, char *key, int key_length,
        char *payload, int payload_size)
{
    int cmp;

    if (node == NULL) {
        node = (cache_object *)Malloc(sizeof(cache_object));
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
        free(node->payload);
        node->payload_size = payload_size;
        node->payload = (char *)Malloc(payload_size);
        memcpy(node->payload, payload, payload_size);
        return node;
    }
    if (cmp < 0)
        node->left = insert(node->left, key, key_length, payload,
                payload_size);
    else
        node->right = insert(node->right, key, key_length, payload,
                payload_size);
    return node;
}

static int keycmp(char *k1, int k1len, char *k2, int k2len)
{
    int cmp = strncmp(k1, k2, MIN(k1len, k2len));

    if (cmp)
        return cmp;
    return k1len - k2len;
}
