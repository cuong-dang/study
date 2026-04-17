// Take a look at the tests, you might have to change the function arguments

pub fn binarySearch(comptime T: type, target: T, items: []const T) ?usize {
    if (items.len == 0) return null;

    var lo: usize = 0;
    var hi: usize = items.len;

    return while (lo < hi) {
        const mid = (lo + hi) / 2;
        if (items[mid] == target) break mid;
        if (items[mid] < target) {
            lo = mid + 1;
        } else {
            hi = mid;
        }
    } else null;
}
