class Solution:
    def exclusiveTime(self, n: int, logs: List[str]) -> List[int]:
        ans = [0] * n
        s = []
        r = []
        for log in logs:
            sf, a, st = log.split(":")
            f, t = int(sf), int(st)
            if a == "start":
                lastStart = f
                if s:
                    fLast, tLast = s.pop()
                    ans[fLast] += t - tLast
                    r.append(fLast)
                s.append((f, t))
            else:
                fLast, tLast = s.pop()
                ans[fLast] += t - tLast + 1
                if r:
                    rf = r.pop()
                    s.append((rf, t + 1))
        return ans
