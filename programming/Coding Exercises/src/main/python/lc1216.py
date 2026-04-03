class Solution:
    def isValidPalindrome(self, s: str, k: int) -> bool:
        cache = {}

        def between(i: int, j: int) -> int:
            if (i, j) in cache:
                return cache[(i, j)]
            if i == j:
                r = 0
            elif i == j-1:
                if s[i] == s[j]:
                    r = 0
                else:
                    r = 1
            elif s[i] == s[j]:
                r = between(i+1, j-1)
            else:
                r = 1 + min(between(i+1, j), between(i, j-1))
            cache[(i, j)] = r
            return cache[(i, j)]

        return between(0, len(s) - 1) <= k
