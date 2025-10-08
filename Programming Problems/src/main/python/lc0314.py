from collections import defaultdict, deque
from typing import Optional, List

from src.main.python.common import TreeNode


class Solution:
    def verticalOrder(self, root: Optional[TreeNode]) -> List[List[int]]:
        m = defaultdict(list)
        q = deque([(root, 0)])

        while q:
            node, column = q.popleft()

            if node is not None:
                m[column].append(node.val)
                q.append((node.left, column - 1))
                q.append((node.right, column + 1))

        return [m[k] for k in sorted(m.keys())]
