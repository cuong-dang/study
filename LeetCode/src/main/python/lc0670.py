class Solution:
    def maximumSwap(self, num: int) -> int:
        s = str(num)
        m = defaultdict(set)
        for i, c in enumerate(s):
            m[c].add(i)
        ss = sorted(s, reverse=True)
        for i, c in enumerate(ss):
            if i not in m[c]:
                # Swap i with max(m[c])
                j = max(m[c])
                sb = list(s)
                sb[i], sb[j] = sb[j], sb[i]
                return int(functools.reduce(lambda a, b: a+b, sb))
            m[c].remove(min(m[c]))
        return num
