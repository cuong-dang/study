const std = @import("std");
const testing = std.testing;

pub fn isArmstrongNumber(num: u128) bool {
    var buf: [39]u8 = undefined;
    const s = std.fmt.bufPrint(&buf, "{}", .{num}) catch return false;
    const len: u128 = @intCast(s.len);
    var sum: u128 = 0;
    for (s) |c| {
        sum += std.math.pow(u128, c - '0', len);
    }
    return sum == num;
}
