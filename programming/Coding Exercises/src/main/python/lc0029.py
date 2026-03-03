class Solution:
    def divide(self, dividend: int, divisor: int) -> int:
        def try_twice(dividend: int, divisor: int) -> (int, int):
            if dividend < divisor:
                return 0, dividend
            q, r = try_twice(dividend, divisor + divisor)
            if q != 0:
                return (q + q, r) if r < divisor else (q + q + 1, r - divisor)
            return 1, dividend - divisor

        if dividend == 0:
            return 0

        ans = 0
        if divisor == 1:
            ans = dividend
        if divisor == -1:
            ans = -dividend
        if ans > 2147483647:
            return 2147483647
        if ans < -2147483648:
            return -2147483648
        if ans != 0:
            return ans

        flip = False
        if dividend < 0 and divisor < 0:
            dividend = -dividend
            divisor = -divisor
        elif divisor < 0:
            flip = True
            divisor = -divisor
        elif dividend < 0:
            flip = True
            dividend = -dividend
        ans, r = try_twice(dividend, divisor)
        return -ans if flip else ans


if __name__ == '__main__':
    print(Solution().divide(-2147483648, 2))
