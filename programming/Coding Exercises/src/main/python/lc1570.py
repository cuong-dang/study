class SparseVector:
    def __init__(self, nums: List[int]):
        self.m = {}
        for i, num in enumerate(nums):
            if num != 0:
                self.m[i] = num

    # Return the dotProduct of two sparse vectors
    def dotProduct(self, vec: 'SparseVector') -> int:
        ans = 0
        for key in self.m.keys():
            if key in vec.m:
                ans += self.m[key] * vec.m[key]
        return ans

# Your SparseVector object will be instantiated and called as such:
# v1 = SparseVector(nums1)
# v2 = SparseVector(nums2)
# ans = v1.dotProduct(v2)
