from typing import List


class Solution:
    def generateParenthesis(self, n: int) -> List[str]:
        def loop(num_lefts, num_rights, s):
            if len(s) == 2 * n:
                ans.append(s)
                return
            if num_lefts < n:
                loop(num_lefts + 1, num_rights, s + "(")
            if num_rights < num_lefts:
                loop(num_lefts, num_rights + 1, s + ")")

        ans = []
        loop(0, 0, "")
        return ans
