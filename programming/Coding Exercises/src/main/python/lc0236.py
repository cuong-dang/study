# Definition for a binary tree node.
# class TreeNode:
#     def __init__(self, x):
#         self.val = x
#         self.left = None
#         self.right = None

class Solution:
    def lowestCommonAncestor(self, root: 'TreeNode', p: 'TreeNode', q: 'TreeNode') -> 'TreeNode':
        def walk(x: TreeNode, target: TreeNode, acc: List[TreeNode]) -> bool:
            acc.append(x)
            if x == target:
                return True
            if x.left:
                if walk(x.left, target, acc):
                    return True
                acc.pop()
            if x.right:
                if walk(x.right, target, acc):
                    return True
                acc.pop()
            return False

        path_p, path_q = [], []
        walk(root, p, path_p)
        walk(root, q, path_q)
        i = 0
        while i < len(path_p) and i < len(path_q) and path_p[i] == path_q[i]:
            i += 1
        return path_p[i - 1]
