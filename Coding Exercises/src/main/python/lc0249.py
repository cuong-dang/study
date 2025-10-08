class Solution:
    def groupStrings(self, strings: List[str]) -> List[List[str]]:
        def dist(a: str, b: str) -> int:
            d = ord(a) - ord(b)
            return d if d > 0 else d + 26

        m = defaultdict(list)
        for s in strings:
            if len(s) == 1:
                if m[1]:
                    m[1][0].append(s)
                else:
                    m[1] = [[s]]
                continue
            groups = m[len(s)]
            if not groups:
                groups.append([s])
                continue
            inserted = False
            for group in groups:
                ss = group[0]
                d = dist(s[0], ss[0])
                belonging = True
                for i, c in enumerate(s):
                    if dist(c, ss[i]) != d:
                        belonging = False
                        break
                if belonging:
                    group.append(s)
                    inserted = True
                    break
            if not inserted:
                groups.append([s])
        ans = []
        for groups in m.values():
            for group in groups:
                ans.append(group)
        return ans
