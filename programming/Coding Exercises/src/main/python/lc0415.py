class Solution:
    def addStrings(self, num1: str, num2: str) -> str:
        if len(num1) < len(num2):
            num1, num2 = num2, num1
        i, j = len(num1) - 1, len(num2) - 1
        ans = [""] * (i + 2)
        c = 0
        while j >= 0:
            s = int(num1[i]) + int(num2[j]) + c
            c = 1 if s > 9 else 0
            s = s % 10 if s > 9 else s
            ans[i+1] = str(s)
            i -= 1
            j -= 1
        while i >= 0:
            s = int(num1[i]) + c
            c = 1 if s > 9 else 0
            s = s % 10 if s > 9 else s
            ans[i+1] = str(s)
            i -= 1
        if c == 1:
            ans[0] = "1"
        return "".join(ans)
