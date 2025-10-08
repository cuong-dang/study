# Definition for a binary tree node.
# class TreeNode:
#     def __init__(self, val=0, left=None, right=None):
#         self.val = val
#         self.left = left
#         self.right = right
class Solution:
    def diameterOfBinaryTree(self, root: Optional[TreeNode]) -> int:
        def walk(x: Optional[TreeNode]) -> (int, int):
            if not x or (not x.left and not x.right):
                return 0, 0
            left, leftComb = walk(x.left) if x.left else (0, 0)
            right, rightComb = walk(x.right) if x.right else (0, 0)
            return (max(left, right) + 1,
                    max(leftComb, rightComb,
                        left + right + 2 if x.left and x.right else 0))

        lr, c = walk(root)
        return max(lr, c)
