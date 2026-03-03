class Solution:
    def __init__(self, nums: List[int]):
        self.h = defaultdict(list)
        for i, n in enumerate(nums):
            self.h[n].append(i)

    def pick(self, target: int) -> int:
        return random.choice(self.h[target])
