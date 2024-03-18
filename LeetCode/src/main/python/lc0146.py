class Node:
    def __init__(self, key:int, val: int, prev: "Node", next: "Node"):
        self.key = key
        self.val = val
        self.prev = prev
        self.next = next

class LRUCache:
    def __init__(self, capacity: int):
        self.capacity = capacity
        self.n = 0
        self.cache = None
        self.tail = None
        self.m = {}

    def moveToFront(self, node: Node):
        if not node.prev:
            return
        if self.tail == node and self.tail.prev:
            self.tail = self.tail.prev
        node.prev.next = node.next
        if node.next:
            node.next.prev = node.prev
        node.next = self.cache
        node.prev = None
        self.cache.prev = node
        self.cache = node


    def get(self, key: int) -> int:
        if key in self.m:
            node = self.m[key]
            self.moveToFront(node)
            return node.val
        return -1

    def put(self, key: int, value: int) -> None:
        if key in self.m:
            node = self.m[key]
            node.val = value
            self.moveToFront(node)
            return
        if self.n < self.capacity:
            node = Node(key, value, None, self.cache)
            if self.cache:
                self.cache.prev = node
            else:
                self.tail = node
            self.cache = node
            self.n += 1
            self.m[key] = node
        else:
            tailKey = self.tail.key
            if self.tail.prev:
                self.tail.prev.next = None
                self.tail = self.tail.prev
            else:
                self.cache = self.tail = None
            del self.m[tailKey]
            self.n -= 1
            self.put(key, value)

# Your LRUCache object will be instantiated and called as such:
# obj = LRUCache(capacity)
# param_1 = obj.get(key)
# obj.put(key,value)
