const std = @import("std");

pub fn isIsogram(str: []const u8) bool {
    var mask: u32 = 0;

    for (str) |c| {
        if (c == ' ' or c == '-') continue;

        const lowered = if (std.ascii.isUpper(c)) std.ascii.toLower(c) else c;
        const shift: u5 = @intCast(lowered - 'a');
        const bit = @as(u32, 1) << shift;

        if ((mask & bit) != 0) return false;
        mask |= bit;
    }
    return true;
}
