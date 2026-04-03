"""
# Definition for a Node.
class Node:
    def __init__(self, val):
        self.val = val
        self.left = None
        self.right = None
        self.parent = None
"""


class Solution:
    def lowestCommonAncestor(self, p: 'Node', q: 'Node') -> 'Node':
        pp, qq = p, q
        while pp != qq:
            pp = pp.parent if pp else q
            qq = qq.parent if qq else p
        return pp
