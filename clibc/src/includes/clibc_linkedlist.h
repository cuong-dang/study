#ifndef CLIBC_LINKEDLIST
#define CLIBC_LINKEDLIST
#include <stddef.h>

typedef struct node {
  void *data;
  struct node *next;
} clibc_node;

typedef struct linkedlist {
  size_t elem_sz;
  clibc_node *head;
} clibc_linkedlist;

clibc_linkedlist *clibc_linkedlist_new(size_t elem_sz);
void clibc_linkedlist_add(clibc_linkedlist *ll, void *elem);
void clibc_linkedlist_free(clibc_linkedlist *ll);

#endif
