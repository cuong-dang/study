class Solution:
    def calculate(self, s: str) -> int:
        s = s.replace(" ", "")
        terms, ops, i = deque(), deque(), 0
        while i < len(s):
            op = s[i]
            curr_n, i = self.n(s, i if s[i].isdigit() else i + 1)
            if op == "+" or op == "-":
                terms.append(curr_n)
                ops.append(op)
            elif op == "*" or op == "/":
                prev_n = terms.pop()
                terms.append(prev_n * curr_n if op == "*" else prev_n // curr_n)
            else:
                terms.append(curr_n)
        while ops:
            op = ops.popleft()
            t1 = terms.popleft()
            t2 = terms.popleft()
            if op == "+":
                terms.appendleft(t1 + t2)
            else:
                terms.appendleft(t1 - t2)
        return terms.pop()

    def n(self, s: str, i: int) -> (int, int):
        sb = ""
        while i < len(s) and s[i].isdigit():
            sb += s[i]
            i += 1
        return int(sb), i
