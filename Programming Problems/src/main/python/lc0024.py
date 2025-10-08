from typing import Optional

from src.main.python.common import ListNode


class Solution:
    def swapPairs(self, head: Optional[ListNode]) -> Optional[ListNode]:
        if head is None or head.next is None:
            return head
        prev, curr = None, head
        swapping = True
        while curr.next is not None:
            if swapping:
                # Special case, first swap
                if prev is None:
                    t = curr.next
                    curr.next = t.next
                    t.next = curr
                    head = t
                    curr = t
                else:
                    t = curr.next
                    curr.next = t.next
                    t.next = curr
                    prev.next = t
                    curr = t
            prev = curr
            curr = curr.next
            swapping = not swapping
        return head


if __name__ == '__main__':
    Solution().swapPairs(ListNode(1, ListNode(2, ListNode(3, ListNode(4)))))
