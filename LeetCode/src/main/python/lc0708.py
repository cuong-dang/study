"""
# Definition for a Node.
class Node:
    def __init__(self, val=None, next=None):
        self.val = val
        self.next = next
"""

class Solution:
    def insert(self, head: 'Optional[Node]', insertVal: int) -> 'Node':
        n = Node(insertVal)
        n.next = n
        if not head:
            return n

        c = head.next
        while True:
            if c == head:
                first, last = head.next, head
                break
            if c.val > c.next.val:
                first, last = c.next, c
                break
            c = c.next
        if first.val >= n.val or last.val <= n.val or first == last:
            last.next = n
            n.next = first
            return head
        p, c = first, first.next
        while True:
            if p.val <= n.val <= c.val:
                p.next = n
                n.next = c
                return head
            p = c
            c = c.next
