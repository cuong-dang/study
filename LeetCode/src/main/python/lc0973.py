from queue import PriorityQueue

class Solution:
    def kClosest(self, points: List[List[int]], k: int) -> List[List[int]]:
        q = PriorityQueue()
        for i, p in enumerate(points):
            w = p[0] * p[0] + p[1] * p[1]
            if i < k:
                q.put((-w, p))
            else:
                ww, pp = q.get()
                if w < -ww:
                    q.put((-w, p))
                else:
                    q.put((ww, pp))
        ans = []
        while not q.empty():
            _, p = q.get()
            ans.append(p)
        return ans
