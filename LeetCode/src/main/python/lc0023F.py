from typing import List, Optional


class ListNode:
    def __init__(self, val=0, next=None):
        self.val = val
        self.next = next

    def __str__(self):
        return f"{self.val} -> {self.next}"


class Solution:
    def mergeKLists(self, lists: List[Optional[ListNode]]) -> Optional[ListNode]:
        lists = list(filter(lambda lst: lst is not None, lists))
        n = len(lists)
        if n == 0:
            return None
        if n == 1:
            return lists[0]
        m = n // 2
        sorted_left = self.mergeKLists(lists[:m])
        sorted_right = self.mergeKLists(lists[m:])
        ans = ListNode()
        tail = ans
        while sorted_left is not None and sorted_right is not None:
            if sorted_left.val < sorted_right.val:
                tail.next = sorted_left
                tail = sorted_left
                sorted_left = sorted_left.next
            else:
                tail.next = sorted_right
                tail = sorted_right
                sorted_right = sorted_right.next
        while sorted_left is not None:
            tail.next = sorted_left
            tail = sorted_left
            sorted_left = sorted_left.next
        while sorted_right is not None:
            tail.next = sorted_right
            tail = sorted_right
            sorted_right = sorted_right.next
        return ans.next


if __name__ == '__main__':
    lists = [ListNode(1, ListNode(4, ListNode(5))), ListNode(1, ListNode(3, ListNode(4))), ListNode(2, ListNode(6))]
    print(Solution().mergeKLists(lists))
