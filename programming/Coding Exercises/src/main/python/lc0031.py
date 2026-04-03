class Solution:
    def nextPermutation(self, nums: List[int]) -> None:
        n = len(nums)
        mark = None
        minIndex = None
        for i in range(n):
            if i < n - 1 and nums[i] < nums[i+1]:
                mark = i
                minIndex = i+1
                continue
            if minIndex and nums[mark] < nums[i] <= nums[minIndex]:
                minIndex = i
        if not minIndex:
            mark = 0
        else:
            nums[mark], nums[minIndex] = nums[minIndex], nums[mark]
            mark += 1
        j = n - 1
        while mark < j:
            nums[mark], nums[j] = nums[j], nums[mark]
            mark += 1
            j -= 1
