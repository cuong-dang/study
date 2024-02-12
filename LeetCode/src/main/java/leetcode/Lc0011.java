package leetcode;

class Lc0011 {
    public int maxArea(int[] height) {
        int left = 0, right = height.length - 1, pLeft = 0, pRight = height.length - 1;
        int max = Math.min(height[left], height[right]) * (right - left);
        while (pLeft < pRight) {
            int newMax = Math.min(height[pLeft], height[pRight]) * (pRight - pLeft);
            if (newMax > max) {
                max = newMax;
            }
            if (height[pLeft] < height[pRight]) {
                pLeft++;
            } else {
                pRight--;
            }
        }
        return max;
    }
}
