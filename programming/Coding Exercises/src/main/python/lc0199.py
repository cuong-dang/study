# Definition for a binary tree node.
# class TreeNode:
#     def __init__(self, val=0, left=None, right=None):
#         self.val = val
#         self.left = left
#         self.right = right
class Solution:
    def rightSideView(self, root: Optional[TreeNode]) -> List[int]:
        def walk(v: Mapping[int, List[int]], level: int, x: TreeNode):
            v[level].append(x.val)
            if x.left:
                walk(v, level+1, x.left)
            if x.right:
                walk(v, level+1, x.right)
        if not root:
            return []
        v = defaultdict(list)
        walk(v, 0, root)
        ans = []
        for vv in v.values():
            ans.append(vv[-1])
        return ans
