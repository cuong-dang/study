"""
# Definition for a Node.
class Node:
    def __init__(self, val, left=None, right=None):
        self.val = val
        self.left = left
        self.right = right
"""


class Solution:
    def treeToDoublyList(self, root: 'Optional[Node]') -> 'Optional[Node]':
        def walk(x: 'Node'):
            nonlocal prev, first
            if x:
                walk(x.left)
                if prev:
                    x.left = prev
                    prev.right = x
                else:
                    first = x
                prev = x
                walk(x.right)

        if not root:
            return None

        prev, first = None, None
        walk(root)
        prev.right = first
        first.left = prev
        return first
