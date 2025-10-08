class Node:
        def __init__(self, val, next):
            self.val = val
            self.next = next

        def __repr__(self):
            return f"Node({str(self.val)})"

class Solution:
    def merge(self, intervals: List[List[int]]) -> List[List[int]]:
        intervals.sort(key=lambda i: i[0])
        ans = None
        for i in range(len(intervals) - 1, -1, -1):
            node = Node(intervals[i], ans)
            ans = node
        p, c = None, ans
        while c.next:
            if c.val[1] >= c.next.val[0]:
                node = Node([c.val[0], max(c.val[1], c.next.val[1])], c.next.next)
                if not p:
                    ans = node
                else:
                    p.next = node
                c = node
            else:
                p = c
                c = c.next
        ans_ = []
        while ans:
            ans_.append(ans.val)
            ans = ans.next
        return ans_
