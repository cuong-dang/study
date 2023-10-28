#ifndef CC_LINKEDLIST
#define CC_LINKEDLIST
#include <stddef.h>

typedef struct node {
  void *data;
  struct node *next;
} cc_linkedlist_node;

typedef struct linkedlist {
  size_t elem_sz;
  cc_linkedlist_node *head;
} cc_linkedlist;

cc_linkedlist *cc_linkedlist_new(size_t elem_sz);
void cc_linkedlist_add(cc_linkedlist *ll, void *elem);
void cc_linkedlist_free(cc_linkedlist *ll);

#endif
