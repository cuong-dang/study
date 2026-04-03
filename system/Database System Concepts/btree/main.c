#include <assert.h>
#include <math.h>
#include <stdlib.h>
#include <string.h>
#include <glib.h>

#define N 4
#define MAXKEYS (N - 1)
#define KEY(i) (((i) - 1) * 2 + 1)
#define POINTER(i) (((i) - 1) * 2)
#define KEYS_TO_LENGTH(k) (2*(k) + 1)
#define MAXKEYS_PLUS_1_SIZE (sizeof(struct node) + 2*sizeof(void *))
#define LEAF_MINKEYS ceil((N - 1) / 2.0)
#define NONLEAF_MINPOINTERS ceil(N / 2.0)

struct node {
    int is_leaf_node;
    int num_keys;
    void *data[2*MAXKEYS + 1];
};

static GHashTable *parent_list;

struct node *make() {
    struct node *btree = calloc(1, sizeof(struct node));
    btree->num_keys = 0;
    btree->is_leaf_node = 1;
    return btree;
}

struct node *find_target_leaf(struct node *root, char *key) {
    int i, cmp;
    char *curr_key;
    struct node *parent;

    if (parent_list) {
        g_hash_table_destroy(parent_list);
    }
    parent_list = g_hash_table_new(g_int_hash, g_int_equal);
    while (1) {
        if (root->is_leaf_node) {
            return root;
        }
        for (i = 1; i <= root->num_keys; ++i) {
            curr_key = (char *) root->data[KEY(i)];
            cmp = strcmp(key, curr_key);
            if (cmp <= 0) {
                break;
            }
        }
        parent = root;
        root = root->data[POINTER(cmp == 0 ? i + 1 : i)];
        g_hash_table_insert(parent_list, root, parent);
    }
}

void copy_data(struct node *dst, struct node *src, int from, int to) {
    int i;

    for (i = from; i <= to; ++i) {
        dst->data[i - from] = src->data[i];
    }
}

void insert_at(struct node* node, int i, int n,
        void *p, char* key, int is_pk_order) {
    int j;

    for (j = n - 4; j >= i; j -= 2) {
        node->data[j + 3] = node->data[j + 1];
        node->data[j + 2] = node->data[j];
    }
    if (j == -1) {
        node->data[2] = node->data[0];
    }
    if (is_pk_order) {
        node->data[i] = p;
        node->data[i + 1] = key;
    } else {
        node->data[i] = key;
        node->data[i + 1] = p;
    }
    ++node->num_keys;
}

void insert_pk_at(struct node* node, int i, int n, void *p, char* key) {
    insert_at(node, i, n, p, key, 1);
}

void insert_kp_at(struct node* node, int i, int n, char *key, void* p) {
    insert_at(node, i, n, p, key, 0);
}

int find_key_i(struct node *node, char *key) {
    int i;

    for (i = 1; i <= node->num_keys; ++i) {
        if (strcmp(key, node->data[KEY(i)]) < 0) {
            return KEY(i);
        }
    }
    return KEY(node->num_keys + 1);
}

void insert_in_leaf(struct node *leaf, int maxkeys, char *key, void *p) {
    int i = find_key_i(leaf, key);

    insert_pk_at(leaf, i - 1, KEYS_TO_LENGTH(maxkeys), p, key);
}

int find_i(struct node *node, void *p) {
    int i;

    for (i = 0; i < KEYS_TO_LENGTH(MAXKEYS); ++i) {
        if (node->data[i] == p) {
            return i;
        }
    }
    return -1;
}

void insert_in_parent(struct node **rootp,
        struct node *leaf1, char *key, struct node *leaf2) {
    struct node *parent, *t, *new_parent;
    int leaf1_i;
    char *new_key;

    if ((parent = g_hash_table_lookup(parent_list, leaf1)) == NULL) {
        *rootp = make();
        (*rootp)->data[KEY(1)] = key;
        (*rootp)->data[POINTER(1)] = leaf1;
        (*rootp)->data[POINTER(2)] = leaf2;
        (*rootp)->num_keys = 1;
        (*rootp)->is_leaf_node = 0;
        return;
    }
    leaf1_i = find_i(parent, leaf1);
    if (parent->num_keys < MAXKEYS) {
        insert_kp_at(parent, leaf1_i + 1, KEYS_TO_LENGTH(MAXKEYS), key, leaf2);
    } else {
        t = calloc(1, MAXKEYS_PLUS_1_SIZE);
        copy_data(t, parent, 0, KEYS_TO_LENGTH(MAXKEYS));
        t->num_keys = parent->num_keys;
        insert_kp_at(t, leaf1_i + 1, KEYS_TO_LENGTH(MAXKEYS + 1), key, leaf2);

        copy_data(parent, t, POINTER(1), POINTER(ceil((N + 1) / 2.0)));
        parent->num_keys = ceil((N + 1) / 2.0) - 1;
        new_parent = make();
        new_parent->is_leaf_node = 0;
        copy_data(new_parent, t,
                  POINTER(ceil((N + 1) / 2.0) + 1), POINTER(N + 1));
        new_parent->num_keys = t->num_keys - parent->num_keys - 1;
        new_key = t->data[KEY((int) ceil((N + 1) / 2.0))];
        insert_in_parent(rootp, parent, new_key, new_parent);
    }
}

void insert(struct node **rootp, char *key, void *p) {
    struct node *leaf, *t, *new_leaf;

    leaf = find_target_leaf(*rootp, key);
    if (leaf->num_keys < MAXKEYS) {
        insert_in_leaf(leaf, MAXKEYS, key, p);
    } else {
        t = calloc(1, MAXKEYS_PLUS_1_SIZE);
        copy_data(t, leaf, 0, KEYS_TO_LENGTH(MAXKEYS));
        t->num_keys = leaf->num_keys;
        insert_in_leaf(t, MAXKEYS + 1, key, p);

        new_leaf = make();
        new_leaf->data[POINTER(N)] = leaf->data[POINTER(N)];
        leaf->data[POINTER(N)] = new_leaf;
        copy_data(leaf, t, POINTER(1), KEY(ceil(N / 2.0)));
        leaf->num_keys = ceil(N / 2.0);
        copy_data(new_leaf, t, POINTER(ceil(N / 2.0) + 1), KEY(N));
        new_leaf->num_keys = N - leaf->num_keys;
        insert_in_parent(rootp, leaf, new_leaf->data[KEY(1)], new_leaf);
    }
}

char *find(struct node *btree, char *key) {
    int i;

    while (!btree->is_leaf_node) {
        for (i = 1; i <= btree->num_keys; ++i) {
            if (strcmp(key, btree->data[KEY(i)]) <= 0) {
                break;
            }
        }
        if (i > btree->num_keys) {
            btree = btree->data[POINTER(btree->num_keys + 1)];
        } else if (strcmp(key, btree->data[KEY(i)]) == 0) {
            btree = btree->data[POINTER(i + 1)];
        } else {
            btree = btree->data[POINTER(i)];
        }
    }
    for (i = 1; i <= btree->num_keys; ++i) {
        if (strcmp(key, btree->data[KEY(i)]) == 0) {
            return btree->data[POINTER(i)];
        }
    }
    return NULL;
}

void delete_entry(struct node **rootp, struct node *node, char *key, void *p) {
    int key_i = find_i(node, key), i, node_i;
    struct node *parent, *prev_sibling, *next_sibling;
    char *mid_key;

    for (; key_i < KEYS_TO_LENGTH(MAXKEYS) - 3; key_i += 2) {
        if (node->is_leaf_node) {
            node->data[key_i] = node->data[key_i + 2];
            node->data[key_i - 1] = node->data[key_i + 1];
        } else {
            node->data[key_i] = node->data[key_i + 2];
            node->data[key_i + 1] = node->data[key_i + 3];
        }
    }
    --node->num_keys;
    i = node->is_leaf_node ? POINTER(node->num_keys + 1) : KEY(node->num_keys + 1);
    for (; i < KEYS_TO_LENGTH(MAXKEYS); ++i) {
        node->data[i] = NULL;
    }

    parent = g_hash_table_lookup(parent_list, node);
    if (!parent && node->data[POINTER(2)] == NULL) {
        *rootp = node->data[POINTER(1)];
    } else if ((node->is_leaf_node && node->num_keys < LEAF_MINKEYS) ||
            (!node->is_leaf_node && node->num_keys < NONLEAF_MINPOINTERS)) {
        node_i = find_i(parent, node);
        prev_sibling = node_i - 2 >= 0 ? parent->data[node_i - 2] : NULL;
        next_sibling = node_i + 2 < KEYS_TO_LENGTH(MAXKEYS) ? parent->data[node_i + 2] : NULL;
        if (prev_sibling && (prev_sibling->num_keys + node->num_keys) <= MAXKEYS) {
            mid_key = parent->data[node_i - 1];
            if (!prev_sibling->is_leaf_node) {
                /* SKIPPED */
            } else {
                for (i = 1; i <= node->num_keys; ++i) {
                    prev_sibling->data[KEY(prev_sibling->num_keys + i)] = node->data[KEY(i)];
                    prev_sibling->data[POINTER(prev_sibling->num_keys + i)] = node->data[POINTER(i)];
                    ++prev_sibling->num_keys;
                }
                prev_sibling->data[POINTER(N)] = node->data[POINTER(N)];
            }
            delete_entry(rootp, parent, mid_key, node);
        } else {
            /* SKIPPED */
        }
    }
}

void delete(struct node **rootp, char *key, void *p) {
    struct node *leaf = find_target_leaf(*rootp, key);
    delete_entry(rootp, leaf, key, p);
}

int main() {
    struct node *btree = make(), *p0, *p1, *p2, *p3, *btree2 = make();

    insert(&btree, "b", "bb");
    assert(btree->num_keys == 1);
    assert(strcmp(btree->data[0], "bb") == 0);
    assert(strcmp(btree->data[1], "b") == 0);

    insert(&btree, "a", "aa");
    assert(btree->num_keys == 2);
    assert(strcmp(btree->data[0], "aa") == 0);
    assert(strcmp(btree->data[1], "a") == 0);
    assert(strcmp(btree->data[2], "bb") == 0);
    assert(strcmp(btree->data[3], "b") == 0);

    insert(&btree, "m", "mm");
    assert(btree->num_keys == 3);
    assert(strcmp(btree->data[0], "aa") == 0);
    assert(strcmp(btree->data[1], "a") == 0);
    assert(strcmp(btree->data[2], "bb") == 0);
    assert(strcmp(btree->data[3], "b") == 0);
    assert(strcmp(btree->data[4], "mm") == 0);
    assert(strcmp(btree->data[5], "m") == 0);

    insert(&btree, "n", "nn");
    assert(btree->num_keys == 1);
    assert(strcmp(btree->data[1], "m") == 0);
    p0 = btree->data[0];
    p1 = btree->data[2];
    assert(p0->num_keys == 2);
    assert(strcmp(p0->data[0], "aa") == 0);
    assert(strcmp(p0->data[1], "a") == 0);
    assert(strcmp(p0->data[2], "bb") == 0);
    assert(strcmp(p0->data[3], "b") == 0);
    assert(p0->data[6] == p1);
    assert(p1->num_keys == 2);
    assert(strcmp(p1->data[0], "mm") == 0);
    assert(strcmp(p1->data[1], "m") == 0);
    assert(strcmp(p1->data[2], "nn") == 0);
    assert(strcmp(p1->data[3], "n") == 0);

    insert(&btree, "c", "cc");
    assert(p0->num_keys == 3);
    assert(strcmp(p0->data[0], "aa") == 0);
    assert(strcmp(p0->data[1], "a") == 0);
    assert(strcmp(p0->data[2], "bb") == 0);
    assert(strcmp(p0->data[3], "b") == 0);
    assert(strcmp(p0->data[4], "cc") == 0);
    assert(strcmp(p0->data[5], "c") == 0);
    insert(&btree, "d", "dd");
    assert(btree->num_keys == 2);
    assert(btree->data[0] == p0);
    assert(p0->num_keys == 2);
    assert(strcmp(p0->data[0], "aa") == 0);
    assert(strcmp(p0->data[1], "a") == 0);
    assert(strcmp(p0->data[2], "bb") == 0);
    assert(strcmp(p0->data[3], "b") == 0);
    assert(strcmp(btree->data[1], "c") == 0);
    p2 = btree->data[2];
    assert(p2->num_keys == 2);
    assert(strcmp(p2->data[0], "cc") == 0);
    assert(strcmp(p2->data[1], "c") == 0);
    assert(strcmp(p2->data[2], "dd") == 0);
    assert(strcmp(p2->data[3], "d") == 0);
    assert(btree->data[4] == p1);
    assert(p1->num_keys == 2);
    assert(strcmp(p1->data[0], "mm") == 0);
    assert(strcmp(p1->data[1], "m") == 0);
    assert(strcmp(p1->data[2], "nn") == 0);
    assert(strcmp(p1->data[3], "n") == 0);
    insert(&btree, "e", "ee");
    assert(p2->num_keys == 3);
    assert(strcmp(p2->data[0], "cc") == 0);
    assert(strcmp(p2->data[1], "c") == 0);
    assert(strcmp(p2->data[2], "dd") == 0);
    assert(strcmp(p2->data[3], "d") == 0);
    assert(strcmp(p2->data[4], "ee") == 0);
    assert(strcmp(p2->data[5], "e") == 0);

    insert(&btree, "f", "ff");
    assert(btree->num_keys == 3);
    assert(strcmp(btree->data[1], "c") == 0);
    assert(strcmp(btree->data[3], "e") == 0);
    assert(strcmp(btree->data[5], "m") == 0);
    assert(btree->data[0] == p0);
    assert(p0->num_keys == 2);
    assert(strcmp(p0->data[0], "aa") == 0);
    assert(strcmp(p0->data[1], "a") == 0);
    assert(strcmp(p0->data[2], "bb") == 0);
    assert(strcmp(p0->data[3], "b") == 0);
    assert(btree->data[2] == p2);
    assert(p2->num_keys == 2);
    assert(strcmp(p2->data[0], "cc") == 0);
    assert(strcmp(p2->data[1], "c") == 0);
    assert(strcmp(p2->data[2], "dd") == 0);
    assert(strcmp(p2->data[3], "d") == 0);
    assert(btree->data[6] == p1);
    assert(p1->num_keys == 2);
    assert(strcmp(p1->data[0], "mm") == 0);
    assert(strcmp(p1->data[1], "m") == 0);
    assert(strcmp(p1->data[2], "nn") == 0);
    assert(strcmp(p1->data[3], "n") == 0);
    p3 = btree->data[4];
    assert(p3->num_keys == 2);
    assert(strcmp(p3->data[0], "ee") == 0);
    assert(strcmp(p3->data[1], "e") == 0);
    assert(strcmp(p3->data[2], "ff") == 0);
    assert(strcmp(p3->data[3], "f") == 0);

    insert(&btree, "g", "gg");
    assert(p3->num_keys == 3);
    assert(strcmp(p3->data[0], "ee") == 0);
    assert(strcmp(p3->data[1], "e") == 0);
    assert(strcmp(p3->data[2], "ff") == 0);
    assert(strcmp(p3->data[3], "f") == 0);
    assert(strcmp(p3->data[4], "gg") == 0);
    assert(strcmp(p3->data[5], "g") == 0);
    insert(&btree, "h", "hh");
    assert(btree->num_keys = 1);
    assert(btree->is_leaf_node == 0);
    assert(strcmp(btree->data[1], "g") == 0);
    p0 = btree->data[0];
    assert(p0->num_keys == 2);
    assert(p0->is_leaf_node == 0);
    assert(strcmp(p0->data[1], "c") == 0);
    assert(strcmp(p0->data[3], "e") == 0);
    p1 = p0->data[0];
    assert(p1->num_keys == 2);
    assert(p1->is_leaf_node == 1);
    assert(strcmp(p1->data[0], "aa") == 0);
    assert(strcmp(p1->data[1], "a") == 0);
    assert(strcmp(p1->data[2], "bb") == 0);
    assert(strcmp(p1->data[3], "b") == 0);
    p2 = p0->data[2];
    assert(p1->data[6] == p2);
    assert(p2->num_keys == 2);
    assert(p2->is_leaf_node == 1);
    assert(strcmp(p2->data[0], "cc") == 0);
    assert(strcmp(p2->data[1], "c") == 0);
    assert(strcmp(p2->data[2], "dd") == 0);
    assert(strcmp(p2->data[3], "d") == 0);
    p3 = p0->data[4];
    assert(p2->data[6] == p3);
    assert(p3->num_keys == 2);
    assert(strcmp(p3->data[0], "ee") == 0);
    assert(strcmp(p3->data[1], "e") == 0);
    assert(strcmp(p3->data[2], "ff") == 0);
    assert(strcmp(p3->data[3], "f") == 0);

    p0 = btree->data[2];
    assert(p0->num_keys == 1);
    assert(p0->is_leaf_node == 0);
    assert(strcmp(p0->data[1], "m") == 0);
    p1 = p0->data[0];
    assert(p3->data[6] == p1);
    assert(p1->num_keys == 2);
    assert(p1->is_leaf_node == 1);
    assert(strcmp(p1->data[0], "gg") == 0);
    assert(strcmp(p1->data[1], "g") == 0);
    assert(strcmp(p1->data[2], "hh") == 0);
    assert(strcmp(p1->data[3], "h") == 0);
    p2 = p0->data[2];
    assert(p1->data[6] == p2);
    assert(p2->num_keys == 2);
    assert(p2->is_leaf_node == 1);
    assert(strcmp(p2->data[0], "mm") == 0);
    assert(strcmp(p2->data[1], "m") == 0);
    assert(strcmp(p2->data[2], "nn") == 0);
    assert(strcmp(p2->data[3], "n") == 0);

    assert(strcmp(find(btree, "a"), "aa") == 0);
    assert(strcmp(find(btree, "b"), "bb") == 0);
    assert(strcmp(find(btree, "c"), "cc") == 0);
    assert(strcmp(find(btree, "d"), "dd") == 0);
    assert(strcmp(find(btree, "e"), "ee") == 0);
    assert(strcmp(find(btree, "f"), "ff") == 0);
    assert(strcmp(find(btree, "g"), "gg") == 0);
    assert(strcmp(find(btree, "h"), "hh") == 0);
    assert(strcmp(find(btree, "m"), "mm") == 0);
    assert(strcmp(find(btree, "n"), "nn") == 0);
    assert(find(btree, "z") == NULL);

    delete(&btree, "m", "mm");
    assert(p2->num_keys == 1);
    assert(strcmp(p2->data[0], "nn") == 0);
    assert(strcmp(p2->data[1], "n") == 0);

    insert(&btree2, "b", "bb");
    insert(&btree2, "a", "aa");
    insert(&btree2, "m", "mm");
    insert(&btree2, "n", "nn");

    delete(&btree2, "n", "nn");
    assert(btree2->num_keys == 3);
    assert(btree2->is_leaf_node);
    assert(strcmp(btree2->data[0], "aa") == 0);
    assert(strcmp(btree2->data[1], "a") == 0);
    assert(strcmp(btree2->data[2], "bb") == 0);
    assert(strcmp(btree2->data[3], "b") == 0);
    assert(strcmp(btree2->data[4], "mm") == 0);
    assert(strcmp(btree2->data[5], "m") == 0);

    return 0;
}
