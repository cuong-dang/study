class Solution:
    def findPeakElement(self, nums: List[int]) -> int:
        def localPeak(i: int, j: int) -> int:
            if i == j:
                return i
            m = (i + j) // 2
            p1 = localPeak(i, m)
            if p1 != m or nums[p1] > nums[p1+1]:
                return p1
            p2 = localPeak(m+1, j)
            if p2 != m or p2 == len(nums) - 1:
                return p2
            if nums[p2] > nums[p2-1]:
                return p2

        return localPeak(0, len(nums) - 1)
