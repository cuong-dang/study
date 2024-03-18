class Solution:
    def largestIsland(self, grid: List[List[int]]) -> int:
        n = len(grid)

        def neighbors(r, c):
            for nr, nc in ((r-1, c), (r+1, c), (r, c-1), (r, c+1)):
                if 0 <= nr < n and 0 <= nc < n:
                    yield nr, nc

        def dfs(r, c, id_):
            ans = 1
            grid[r][c] = id_
            for nr, nc in neighbors(r, c):
                if grid[nr][nc] == 1:
                    ans += dfs(nr, nc, id_)
            return ans

        m = {}
        id_ = 2
        for r in range(n):
            for c in range(n):
                if grid[r][c] == 1:
                    m[id_] = dfs(r, c, id_)
                    id_ += 1

        ans = max(m.values() or [0])
        for r in range(n):
            for c in range(n):
                if grid[r][c] == 0:
                    seen = {grid[nr][nc] for nr, nc in neighbors(r, c) if grid[nr][nc] > 1}
                    ans = max(ans, 1 + sum(m[i] for i in seen))
        return ans
