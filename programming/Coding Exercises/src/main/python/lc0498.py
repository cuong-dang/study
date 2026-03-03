class Solution:
    def findDiagonalOrder(self, mat: List[List[int]]) -> List[int]:
        ans = []
        goingUp = True
        i, j = 0, 0
        n, m = len(mat), len(mat[0])
        while True:
            ans.append(mat[i][j])
            if i == n-1 and j == m-1:
                break
            if goingUp and not (0 <= i-1 < n and 0 <= j+1 < m):
                goingUp = False
                if i-1 < 0 and j+1 < m:
                    j += 1
                else:
                    i += 1
            elif not goingUp and not (0 <= i+1 < n and 0 <= j-1 < m):
                goingUp = True
                if i+1 >= n:
                    j += 1
                else:
                    i += 1
            elif goingUp:
                i -= 1
                j += 1
            else:
                i += 1
                j -= 1
        return ans
