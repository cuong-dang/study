from typing import Optional

from src.main.python.common import ListNode


class Solution:
    def removeNthFromEnd(self, head: Optional[ListNode], n: int) -> Optional[ListNode]:
        prev, curr, d = None, head, 0
        while curr is not None:
            curr = curr.next
            if d == n:
                prev = prev.next if prev is not None else head
            else:
                d += 1
        if prev is None:
            head = head.next
        else:
            prev.next = prev.next.next if prev.next is not None else None
        return head
