from typing import List


class Solution:
    def letterCombinations(self, digits: str) -> List[str]:
        st = {
            "2": ["a", "b", "c"],
            "3": ["d", "e", "f"],
            "4": ["g", "h", "i"],
            "5": ["j", "k", "l"],
            "6": ["m", "n", "o"],
            "7": ["p", "q", "r", "s", "t"],
            "8": ["t", "u", "v"],
            "9": ["w", "x", "y", "z"],
        }
        if digits == "":
            return []
        if len(digits) == 1:
            return st[digits[0]]
        r = self.letterCombinations(digits[1:])
        ans = []
        for letter in st[digits[0]]:
            ans += list(map(lambda x: letter + x, r))
        return ans
