#include <assert.h>

#define MAX_NODES 16

typedef struct Node {
  struct Node *left;
  struct Node *right;
} Node;

typedef struct NodeHeight {
  struct Node *k;
  int v;
} NodeHeight;

int height(Node *root) {
  int h = 0, i = 0, j = 1;
  NodeHeight q[MAX_NODES], curr;

  q[0].k = root;
  q[0].v = 0;
  while (i < j) {
    curr = q[i++];
    if (curr.v > h) {
      h = curr.v;
    }
    if (curr.k->left) {
      q[j].k = curr.k->left;
      q[j].v = curr.v + 1;
      j++;
    }
    if (curr.k->right) {
      q[j].k = curr.k->right;
      q[j].v = curr.v + 1;
      j++;
    }
  }
  return h;
}

int main() {
  Node l1 = {0, 0}, l2 = {0, 0}, i1 = {&l1, &l2};
  Node l3 = {0, 0}, i3 = {&l3, 0}, i4 = {0, &i3};
  Node i2 = {&i1, &i4};

  assert(height(&l1) == 0);
  assert(height(&i1) == 1);
  assert(height(&i2) == 3);
  assert(height(&i4) == 2);
}
