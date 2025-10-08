class Solution:
    def copyRandomList(self, head: "Optional[Node]") -> "Optional[Node]":
        m = {}

        def copyNode(node: Node) -> Node:
            if id(node) in m:
                return m[id(node)]
            newNode = Node(node.val, None, None)
            m[id(node)] = newNode
            if node.next:
                newNode.next = copyNode(node.next)
            if node.random:
                newNode.random = copyNode(node.random)
            return newNode

        if not head:
            return None
        return copyNode(head)
