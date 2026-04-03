const std = @import("std");

pub fn score(s: []const u8) u32 {
    var res: u32 = 0;
    for (s) |c| {
        const add: u32 = switch (if (std.ascii.isLower(c)) std.ascii.toUpper(c) else c) {
            'A', 'E', 'I', 'O', 'U', 'L', 'N', 'R', 'S', 'T' => 1,
            'D', 'G' => 2,
            'B', 'C', 'M', 'P' => 3,
            'F', 'H', 'V', 'W', 'Y' => 4,
            'K' => 5,
            'J', 'X' => 8,
            'Q', 'Z' => 10,
            else => unreachable,
        };
        res += add;
    }
    return res;
}
