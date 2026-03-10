const std = @import("std");

pub fn isPangram(str: []const u8) bool {
    var mask: u32 = 0;

    for (str) |c| {
        if (!std.ascii.isAlphabetic(c)) continue;
        const lowered = if (std.ascii.isUpper(c)) std.ascii.toLower(c) else c;
        const shift: u5 = @intCast(lowered - 'a');
        mask |= @as(u32, 1) << shift;
    }
    return mask == 0x3ffffff;
}
