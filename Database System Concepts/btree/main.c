#include <assert.h>
#include <math.h>
#include <stdlib.h>
#include <string.h>
#include <glib.h>

#define FANOUT 4
#define MAXKEYS (FANOUT - 1)
#define KEY(i) ((i)*2 + 1)
#define POINTER(i) ((i)*2)

struct node {
    int is_leaf_node;
    int num_keys;
    void *data[2*MAXKEYS + 1];
};

static GHashTable *parent_list;

struct node *make() {
    struct node *btree = malloc(sizeof(struct node));
    btree->num_keys = 0;
    btree->is_leaf_node = 1;
    return btree;
}

struct node *find_target_leaf(struct node *node, char *key) {
    int i;
    char *curr_key;
    struct node *parent;

    parent_list = g_hash_table_new(g_int_hash, g_int_equal);
    while (1) {
        if (node->is_leaf_node) {
            return node;
        }
        for (i = 0; i < node->num_keys; ++i) {
            curr_key = (char *) node->data[KEY(i)];
            if (strcmp(key, curr_key) < 0) {
                parent = node;
                node = (struct node *) node->data[POINTER(i)];
                g_hash_table_insert(parent_list, parent, node);
                break;
            }
        }
        node = (struct node *) node->data[2 * i];
    }
}

void copy_leaf(struct node *dst, struct node *src, int from, int n) {
    int i;

    for (i = from; i < n; ++i) {
        dst->data[KEY(i - from)] = src->data[KEY(i)];
        dst->data[POINTER(i - from)] = src->data[POINTER(i)];
    }
    dst->num_keys = n - from;
}

void insert_in_leaf(struct node *leaf, char *key, void *p) {
    int i, j;

    for (i = 0; i < leaf->num_keys; ++i) {
        if (strcmp(key, leaf->data[KEY(i)]) < 0) {
            break;
        }
    }
    for (j = leaf->num_keys - 1; j >= i; --j) {
        leaf->data[KEY(j + 1)] = leaf->data[KEY(j)];
        leaf->data[POINTER(j + 1)] = leaf->data[POINTER(j)];
    }
    leaf->data[KEY(i)] = key;
    leaf->data[POINTER(i)] = p;
    ++leaf->num_keys;
}

void insert_in_parent(struct node **root,
        struct node *leaf1, char *key, struct node *leaf2) {
    struct node *parent, *t, *new_parent;
    int lparent_last_pointer_i;
    char *new_key;

    if ((parent = g_hash_table_lookup(parent_list, leaf1)) == NULL) {
        *root = make();
        (*root)->data[KEY(0)] = key;
        (*root)->data[POINTER(0)] = leaf1;
        (*root)->data[POINTER(1)] = leaf2;
        (*root)->num_keys = 1;
        (*root)->is_leaf_node = 0;
        return;
    }
    if (parent->num_keys < MAXKEYS) {
        insert_in_leaf(parent, key, leaf2);
    } else {
        t = malloc(sizeof(struct node) + 2*sizeof(void *));
        t->is_leaf_node = 1;
        copy_leaf(t, parent, 0, parent->num_keys);
        insert_in_leaf(t, key, leaf2);
        
        copy_leaf(parent, t, 0, ceil(FANOUT / 2.0));
        new_parent = make();
        new_parent->is_leaf_node = 0;
        copy_leaf(parent, t, 0, ceil(FANOUT / 2.0));
        lparent_last_pointer_i = POINTER(parent->num_keys) + 2;
        parent->data[lparent_last_pointer_i] = t->data[lparent_last_pointer_i];
        copy_leaf(new_parent, t, ceil((FANOUT + 1) / 2.0) + 1, t->num_keys);
        new_key = t->data[KEY((int) ceil((FANOUT + 1) / 2.0))];
        insert_in_parent(root, parent, new_key, new_parent);
    }
}

void insert(struct node **root, char *key, void *p) {
    struct node *leaf, *t, *new_leaf;

    leaf = find_target_leaf(*root, key);
    if (leaf->num_keys < MAXKEYS) {
        insert_in_leaf(leaf, key, p);
    } else {
        t = malloc(sizeof(struct node) + 2*sizeof(void *));
        t->is_leaf_node = 1;
        copy_leaf(t, leaf, 0, leaf->num_keys);
        insert_in_leaf(t, key, p);

        new_leaf = make();
        new_leaf->data[POINTER(MAXKEYS)] = leaf->data[POINTER(MAXKEYS)];
        leaf->data[POINTER(MAXKEYS)] = new_leaf;
        copy_leaf(leaf, t, 0, ceil(FANOUT / 2.0));
        copy_leaf(new_leaf, t, ceil(FANOUT / 2.0), t->num_keys);
        insert_in_parent(root, leaf, new_leaf->data[KEY(0)], new_leaf);
    }
}

int main() {
    struct node *root = make(), *left, *right;

    insert(&root, "b", "bb");
    assert(root->num_keys == 1);
    assert(strcmp(root->data[0], "bb") == 0);
    assert(strcmp(root->data[1], "b") == 0);

    insert(&root, "a", "aa");
    assert(root->num_keys == 2);
    assert(strcmp(root->data[0], "aa") == 0);
    assert(strcmp(root->data[1], "a") == 0);
    assert(strcmp(root->data[2], "bb") == 0);
    assert(strcmp(root->data[3], "b") == 0);

    insert(&root, "g", "gg");
    assert(root->num_keys == 3);
    assert(strcmp(root->data[0], "aa") == 0);
    assert(strcmp(root->data[1], "a") == 0);
    assert(strcmp(root->data[2], "bb") == 0);
    assert(strcmp(root->data[3], "b") == 0);
    assert(strcmp(root->data[4], "gg") == 0);
    assert(strcmp(root->data[5], "g") == 0);

    insert(&root, "h", "hh");
    assert(root->num_keys == 1);
    assert(strcmp(root->data[1], "g") == 0);
    left = root->data[0];
    right = root->data[2];
    assert(left->num_keys == 2);
    assert(strcmp(left->data[0], "aa") == 0);
    assert(strcmp(left->data[1], "a") == 0);
    assert(strcmp(left->data[2], "bb") == 0);
    assert(strcmp(left->data[3], "b") == 0);
    assert(left->data[6] == right);
    assert(right->num_keys == 2);
    assert(strcmp(right->data[0], "gg") == 0);
    assert(strcmp(right->data[1], "g") == 0);
    assert(strcmp(right->data[2], "hh") == 0);
    assert(strcmp(right->data[3], "h") == 0);

    insert(&root, "c", "cc");
    insert(&root, "d", "dd");
    insert(&root, "e", "ee");

    return 0;
}
