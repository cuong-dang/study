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
        if len(lists) == 0:
            return None
        if all(map(lambda lst: lst.next is None, lists)):
            sorted_lst = sorted(list(map(lambda lst: lst.val, lists)), reverse=True)
            head = None
            for val in sorted_lst:
                head = ListNode(val, head)
            return head
        heads = []
        for idx, lst in enumerate(lists):
            heads.append((idx, lst))
        heads.sort(key=lambda t: t[1].val)
        lists[heads[0][0]] = lists[heads[0][0]].next
        next_ = list(filter(lambda lst: lst is not None, lists))
        return ListNode(heads[0][1].val, self.mergeKLists(next_))


if __name__ == '__main__':
    lists = [ListNode(1, ListNode(4, ListNode(5))), ListNode(1, ListNode(3, ListNode(4))), ListNode(2, ListNode(6))]
    print(Solution().mergeKLists(lists))
