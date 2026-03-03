class MovingAverage:
    def __init__(self, size: int):
        self.size = size
        self.a = deque()

    def next(self, val: int) -> float:
        if len(self.a) == self.size:
            self.a.popleft()
        self.a.append(val)
        return sum(self.a) / len(self.a)

# Your MovingAverage object will be instantiated and called as such:
# obj = MovingAverage(size)
# param_1 = obj.next(val)
