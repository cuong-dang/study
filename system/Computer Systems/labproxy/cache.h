#ifndef __CACHE_H__
#define __CACHE_H__

typedef struct node {
    int age;
    int is_evicted;
    char *key;
    int key_length;
    char *payload;
    int payload_size;
    struct node *left;
    struct node *right;
} cache_object;

typedef struct {
    int max_size;
    int max_object_size;
    int cached_bytes;
    cache_object *root;
} cache;

cache *make_cache(int max_size, int max_object_size);
cache_object *cache_lookup(cache *c, char *key);
void cache_insert(cache *c, char *key, int key_length, char *payload,
        int payload_size, int age);
void cache_renew_object(cache_object *op, int age);

#endif /* __CACHE_H__ */
