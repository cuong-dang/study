# Definition for a binary tree node.
# class TreeNode:
#     def __init__(self, val=0, left=None, right=None):
#         self.val = val
#         self.left = left
#         self.right = right
class Solution:
    def rangeSumBST(self, root: Optional[TreeNode], low: int, high: int) -> int:
        ans = 0

        def walk(x: TreeNode):
            nonlocal ans

            if low <= x.val <= high:
                ans += x.val
            if x.val > low and x.left:
                walk(x.left)
            if x.val < high and x.right:
                walk(x.right)

        if root:
            walk(root)
        return ans
