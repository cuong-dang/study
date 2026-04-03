class Solution:
    def minAddToMakeValid(self, s: str) -> int:
        opens, closes = 0, 0
        for c in s:
            if c == "(":
                opens += 1
            elif c == ")" and opens:
                opens -= 1
            else:
                closes += 1
        return opens + closes
