class Solution:
    def customSortString(self, order: str, s: str) -> str:
        counter = collections.Counter(s)
        lookup = set()
        sb = ""
        for c in order:
            lookup.add(c)
            if c in counter:
                sb += c * counter[c]
        for c in s:
            if c not in lookup:
                sb += c
        return sb
