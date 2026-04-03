#include <algorithm>
#include <cassert>
#include <cstdlib>
#include <set>
#include <vector>

typedef struct BinNode {
  int val;
  struct BinNode *left;
  struct BinNode *right;
} BinNode;

bool check_nodes(const std::vector<int> &inorder, int l1, int r1,
                 const std::vector<int> &postorder, int l2, int r2) {
  std::set<int> s{};
  int i;

  assert(r1 - l1 == r2 - l2);
  for (i = l1; i <= r1; i++) {
    s.insert(inorder[i]);
  }
  for (i = l2; i <= r2; i++) {
    if (s.find(postorder[i]) == s.end()) {
      return false;
    }
  }
  return true;
}

BinNode *from_inorder_postorder(const std::vector<int> &inorder, int l1, int r1,
                                const std::vector<int> &postorder, int l2,
                                int r2) {
  BinNode *root;
  int root_inorder_at, num_left_nodes, num_right_nodes, left_l1, left_r1,
      left_l2, left_r2, right_r1, right_l1, right_l2, right_r2;

  if (l1 == r1) {
    assert(l2 == r2);
    root = (BinNode *)malloc(sizeof(BinNode));
    root->val = inorder[l1];
    root->left = nullptr;
    root->right = nullptr;
    return root;
  }
  root = (BinNode *)malloc(sizeof(BinNode));
  root->val = postorder[r2];
  root_inorder_at =
      std::find(inorder.begin(), inorder.end(), root->val) - inorder.begin();
  num_left_nodes = root_inorder_at - l1;
  num_right_nodes = r2 - l2 - num_left_nodes;
  left_l1 = l1;
  left_r1 = root_inorder_at - 1;
  left_l2 = l2;
  left_r2 = l2 + num_left_nodes - 1;
  right_l1 = root_inorder_at + 1;
  right_r1 = right_l1 + num_right_nodes - 1;
  right_l2 = l2 + num_left_nodes;
  right_r2 = r2 - 1;

  if (!check_nodes(inorder, left_l1, left_r1, postorder, left_l2, left_r2))
    return nullptr;
  if (!check_nodes(inorder, right_l1, right_r1, postorder, right_l2, right_r2))
    return nullptr;
  if (num_left_nodes != 0)
    root->left = from_inorder_postorder(inorder, left_l1, left_r1, postorder,
                                        left_l2, left_r2);
  if (num_right_nodes != 0)
    root->right = from_inorder_postorder(inorder, right_l1, right_r1, postorder,
                                         right_l2, right_r2);
  return root;
}

BinNode *from_inorder_postorder(const std::vector<int> &inorder,
                                const std::vector<int> &postorder) {
  return from_inorder_postorder(inorder, 0, inorder.size() - 1, postorder, 0,
                                postorder.size() - 1);
}

int main() {
  std::vector<int> io1{1, 0, 4}, po1{1, 4, 0};
  std::vector<int> io2{9, 3, 1, 0, 4, 2, 7, 6, 8, 5},
      po2{9, 1, 4, 0, 3, 6, 7, 5, 8, 2};
  std::vector<int> io3{1, 0, 2}, po3{2, 1, 0};
  BinNode *t;

  t = from_inorder_postorder(io1, po1);
  assert(t->val == 0);
  assert(t->left->val == 1);
  assert(t->left->left == nullptr);
  assert(t->left->right == nullptr);
  assert(t->right->val == 4);
  assert(t->right->left == nullptr);
  assert(t->right->right == nullptr);

  t = from_inorder_postorder(io2, po2);
  assert(t->val == 2);
  assert(t->left->val == 3);
  assert(t->right->val == 8);
  assert(t->left->left->val == 9);
  assert(t->left->right->val == 0);
  assert(t->left->left->left == nullptr);
  assert(t->left->left->right == nullptr);
  assert(t->left->right->left->val == 1);
  assert(t->left->right->right->val == 4);
  assert(t->left->right->left->left == nullptr);
  assert(t->left->right->left->right == nullptr);
  assert(t->left->right->right->left == nullptr);
  assert(t->left->right->right->right == nullptr);
  assert(t->right->left->val == 7);
  assert(t->right->right->val == 5);
  assert(t->right->left->left == nullptr);
  assert(t->right->left->right->val == 6);
  assert(t->right->left->right->left == nullptr);
  assert(t->right->left->right->right == nullptr);
  assert(t->right->right->left == nullptr);
  assert(t->right->right->right == nullptr);

  t = from_inorder_postorder(io3, po3);
  assert(t == nullptr);
}
