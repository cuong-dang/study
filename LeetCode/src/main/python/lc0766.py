class Solution:
    def isToeplitzMatrix(self, matrix: List[List[int]]) -> bool:
        def toeplitz_diag(i: int, j: int) -> bool:
            pivot = matrix[i][j]
            while i < len(matrix) and j < len(matrix[0]):
                if matrix[i][j] != pivot:
                    return False
                i += 1
                j += 1
            return True

        i, j = len(matrix) - 1, 0
        while i != 0 or j != len(matrix[0]) - 1:
            if not toeplitz_diag(i, j):
                return False
            if i != 0:
                i -= 1
            else:
                j += 1
        return True
