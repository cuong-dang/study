class Solution:
    def findKthLargest(self, nums: List[int], k: int) -> int:
        return self.qselect(nums, k)

    def qselect(self, nums: List[int], k: int) -> int:
        p = random.choice(nums)
        left, mid, right = [], [], []
        for n in nums:
            if n > p:
                left.append(n)
            elif n == p:
                mid.append(n)
            else:
                right.append(n)
        if k <= len(left):
            return self.qselect(left, k)
        if k > len(left) + len(mid):
            return self.qselect(right, k - len(left) - len(mid))
        return p
