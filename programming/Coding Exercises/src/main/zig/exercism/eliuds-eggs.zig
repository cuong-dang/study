pub fn eggCount(number: usize) usize {
    var n: usize = number;
    var count: usize = 0;
    while (n > 0) : (n >>= 1) {
        if (n & 1 == 1) count += 1;
    }
    return count;
}
