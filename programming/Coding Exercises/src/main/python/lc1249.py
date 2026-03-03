from typing import Set


class Solution:
    def minRemoveToMakeValid(self, s: str) -> str:
        def rm(a: Set[int]) -> str:
            if not a:
                return s

            ss = ""
            for k, c in enumerate(s):
                if k in a:
                    continue
                ss += c
            return ss

        opens, closes = [], set()

        for i, c in enumerate(s):
            if c == "(":
                opens.append(i)
            elif c == ")" and opens:
                opens.pop()
            elif c == ")":
                closes.add(i)
        return rm(set(opens).union(closes))
