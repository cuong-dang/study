from typing import Optional

from src.main.python.common import ListNode


class Solution:
    def removeNthFromEnd(self, head: Optional[ListNode], n: int) -> Optional[ListNode]:
        len = 0
        curr = head
        while curr is not None:
            len += 1
            curr = curr.next
        prev, curr = None, head
        step = len - n
        while step != 0:
            prev = curr
            curr = curr.next
            step -= 1
        if prev is None:
            return head.next
        prev.next = curr.next
        return head
