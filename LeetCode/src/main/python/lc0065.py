class Solution:
    def isNumber(self, s: str) -> bool:
        def areDecimalDigits(s: str) -> bool:
            idx = s.find(".")
            if idx == -1:
                return areDigits(s)
            left, right = s[:idx], s[idx+1:]
            leftOk = True if not left else areDigits(left)
            rightOk = True if not right else areDigits(right)
            return (left or right) and leftOk and rightOk

        def isDecimal(s: str) -> bool:
            if s[0] == "+" or s[0] == "-":
                return areDecimalDigits(s[1:])
            return areDecimalDigits(s)

        def areDigits(s: str) -> bool:
            if not s:
                return False
            for c in s:
                if not c.isdigit():
                    return False
            return True

        def isInteger(s: str) -> bool:
            if s[0] == "+" or s[0] == "-":
                return areDigits(s[1:])
            return areDigits(s)

        if "E" in s:
            s = s.replace("E", "e")
        idx = s.find("e")
        if idx != -1:
            left, right = s[:idx], s[idx+1:]
            return (left and right) and (isDecimal(left) or isInteger(left)) and isInteger(right)
        else:
            return isDecimal(s) or isInteger(s)
