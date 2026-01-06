class Stack:
    def __init__(self, name):
        self.s = []
        self.name = name

    def push(self, a):
        self.s.append(a)

    def pop(self):
        return self.s.pop()

    def is_empty(self):
        return len(self.s) == 0


def iterative_toh(n):
    # Init poles
    s, a, d = Stack("Src"), Stack("Aux"), Stack("Dst")
    for i in range(n, 0, -1):
        s.push(i)
    num_moves = pow(2, n) - 1
    if n % 2 == 0:
        a, d = d, a
    for i in range(1, num_moves + 1):
        if i % 3 == 1:
            make_legal_move(s, d)
        elif i % 3 == 2:
            make_legal_move(s, a)
        else:
            make_legal_move(a, d)


def make_legal_move(p1, p2):
    if p2.is_empty():
        from_ = p1
        to_ = p2
        d = p1.pop()
    elif p1.is_empty():
        from_ = p2
        to_ = p1
        d = p2.pop()
    else:
        d1, d2 = p1.pop(), p2.pop()
        if d1 < d2:
            from_ = p1
            to_ = p2
            d = d1
            p2.push(d2)
        else:
            from_ = p2
            to_ = p1
            d = d2
            p1.push(d1)
    print(f"Move disk {d} from {from_.name} to {to_.name}")
    to_.push(d)


iterative_toh(4)
