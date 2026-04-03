const std = @import("std");

pub fn twoFer(buffer: []u8, name: ?[]const u8) ![]u8 {
    if (name) |n| {
        return std.fmt.bufPrint(buffer, "One for {s}, one for me.", .{n});
    } else {
        return std.fmt.bufPrint(buffer, "One for you, one for me.", .{});
    }
}
