class Solution:
    def shortestPathBinaryMatrix(self, grid: List[List[int]]) -> int:
        def examine(i: int, j: int):
            nonlocal p, q, n
            if 0 <= i < n and 0 <= j < n and grid[i][j] != 1:
                if (i, j) not in p:
                    p[(i, j)] = steps + 1
                    q.append((i, j))
                elif p[(i, j)] > steps + 1:
                    p[(i, j)] = steps + 1

        if grid[0][0] != 0:
            return -1
        n = len(grid)
        p = {(0, 0): 1}
        q = deque()
        q.append((0, 0))
        while q:
            i, j = q.popleft()
            steps = p[(i, j)]
            examine(i-1, j-1)
            examine(i-1, j)
            examine(i-1, j+1)
            examine(i, j-1)
            examine(i, j+1)
            examine(i+1, j-1)
            examine(i+1, j)
            examine(i+1, j+1)
        if (n-1, n-1) in p:
            return p[(n-1, n-1)]
        return -1
