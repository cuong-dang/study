class Solution:
    def findBuildings(self, heights: List[int]) -> List[int]:
        i = len(heights) - 1
        max_height = 0
        ans = deque()
        while i >= 0:
            if heights[i] > max_height:
                ans.appendleft(i)
                max_height = heights[i]
            i -= 1
        return list(ans)
