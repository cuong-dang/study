class Solution:
    def removeInvalidParentheses(self, s: str) -> List[str]:
        opens, closes = 0, 0
        for c in s:
            if c == "(":
                opens += 1
            elif c == ")":
                if opens:
                    opens -= 1
                else:
                    closes += 1

        cache = {}

        def rm(s: str, c: str, n: int) -> Set[str]:
            if (s, c, n) in cache:
                return cache[(s, c, n)]
            if n == 0:
                return [s]
            ans = set()
            for i, cc in enumerate(s):
                if cc == c:
                    ans.update(rm(s[:i] + s[i + 1:], c, n - 1))
            cache[(s, c, n)] = ans
            return ans

        opensRmed = rm(s, "(", opens)
        cands = set()
        for openRmed in opensRmed:
            cands.update(rm(openRmed, ")", closes))

        def isValid(s: str) -> bool:
            opens = 0
            for c in s:
                if c == "(":
                    opens += 1
                elif c == ")":
                    if opens:
                        opens -= 1
                    else:
                        return False
            return opens == 0

        ans = []
        for cand in cands:
            if isValid(cand):
                ans.append(cand)
        return ans
