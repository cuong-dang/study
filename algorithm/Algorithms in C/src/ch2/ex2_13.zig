const expectEqual = @import("std").testing.expectEqual;

fn ceilLgLg(n: usize) usize {
    if (n <= 2) return 0;
    var lg: usize = 0;
    var x = n;
    while (x > 1) {
        x = (x + 1) / 2;
        lg += 1;
    }

    var ans: usize = 0;
    x = lg;
    while (x > 1) {
        x = (x + 1) / 2;
        ans += 1;
    }

    return ans;
}

test ceilLgLg {
    for (0..3) |i| {
        try expectEqual(0, ceilLgLg(i));
    }
    for (3..5) |i| {
        try expectEqual(1, ceilLgLg(i));
    }
    for (5..17) |i| {
        try expectEqual(2, ceilLgLg(i));
    }
    for (17..256) |i| {
        try expectEqual(3, ceilLgLg(i));
    }
}
