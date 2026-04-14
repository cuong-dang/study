pub fn squareRoot(radicand: usize) usize {
    var lo: usize = 0;
    var hi: usize = radicand + 1;
    while (lo != (hi - 1)) {
        const mid = (lo + hi) / 2;
        if (mid * mid <= radicand) {
            lo = mid;
        } else {
            hi = mid;
        }
    }
    return lo;
}
