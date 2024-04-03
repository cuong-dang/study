class Solution:
    def closestValue(self, root: Optional[TreeNode], target: float) -> int:
        ans, dist = root.val, root.val - target

        def walk(x: TreeNode):
            nonlocal ans, dist

            xDist = x.val - target
            if abs(xDist) < abs(dist):
                ans, dist = x.val, xDist
            elif abs(xDist) == abs(dist) and x.val < ans:
                ans = x.val
            if xDist > 0 and x.left:
                walk(x.left)
            elif xDist < 0 and x.right:
                walk(x.right)

        walk(root)
        return ans
