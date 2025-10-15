class Solution:
    def verticalTraversal(self, root: Optional[TreeNode]) -> List[List[int]]:
        m = defaultdict(lambda: defaultdict(list))
        q = deque()
        q.append((root, 0, 0))

        while q:
            x, row, col = q.popleft()
            if not x:
                continue
            m[col][row].append(x.val)
            q.append((x.left, row + 1, col - 1))
            q.append((x.right, row + 1, col + 1))

        ans = []
        for col in sorted(m.keys()):
            subAns = []
            for vals in m[col].values():
                subAns.extend(sorted(vals))
            ans.append(subAns)
        return ans
