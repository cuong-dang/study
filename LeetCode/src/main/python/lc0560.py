class Solution:
    def subarraySum(self, nums: List[int], k: int) -> int:
        ans = 0
        m = defaultdict(int)
        s = 0
        for i, _ in enumerate(nums):
            s += nums[i]
            if s == k:
                ans += 1
            if s - k in m:
                ans += m[s - k]
            m[s] += 1
        return ans
