from typing import Optional

from src.main.python.common import ListNode


def reverseKGroup(head: Optional[ListNode], k: int) -> Optional[ListNode]:
    rev = None
    curr = head
    rev_end = None
    while True:
        # Look ahead
        count = 0
        count_curr = curr
        while count_curr:
            count += 1
            count_curr = count_curr.next
        if count < k:
            rev_end.next = curr
            break
        # Swap
        first_swap = False
        if rev is None:
            first_swap = True
        num_swaps = k
        rev = None
        while num_swaps != 0:
            t = curr.next
            curr.next = rev
            rev = curr
            curr = t
            num_swaps -= 1
        if rev_end is None:
            rev_end = rev
        else:
            rev_end.next = rev
        while rev_end.next:
            rev_end = rev_end.next
        rev_end.next = curr
        if first_swap:
            head = rev

    return head


if __name__ == '__main__':
    print(reverseKGroup(ListNode(1, ListNode(2, ListNode(3, ListNode(4, ListNode(5))))), 3))
