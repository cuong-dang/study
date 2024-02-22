from typing import List


class Solution:
    def findSubstring(self, s: str, words: List[str]) -> List[int]:
        def isSubstring(s: str, words: List[str]) -> bool:
            if s == "" and len(words) == 0:
                return True
            for word in words:
                if s.startswith(word):
                    rest = words.copy()
                    rest.remove(word)
                    return isSubstring(s[len(word):], rest)

        cache = set()
        wlen = len(words[0])
        ans = []
        for i in range(len(s) - wlen * len(words) + 1):
            ss = s[i:i + wlen * len(words)]
            if ss in cache:
                ans.append(i)
            elif isSubstring(ss, words):
                cache.add(ss)
                ans.append(i)
        return ans
