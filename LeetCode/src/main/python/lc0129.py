# Definition for a binary tree node.
# class TreeNode:
#     def __init__(self, val=0, left=None, right=None):
#         self.val = val
#         self.left = left
#         self.right = right
class Solution:
    def sumNumbers(self, root: Optional[TreeNode]) -> int:
        def walk(x: TreeNode) -> List[str]:
            if not x.left and not x.right:
                return [str(x.val)]
            lefts = walk(x.left) if x.left else []
            rights = walk(x.right) if x.right else []
            return list(map(lambda s: str(x.val) + s, lefts + rights))

        return sum(map(int, walk(root)))
