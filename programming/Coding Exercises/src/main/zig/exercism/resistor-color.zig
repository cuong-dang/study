pub const ColorBand = enum(usize) {
    black,
    brown,
    red,
    orange,
    yellow,
    green,
    blue,
    violet,
    grey,
    white,
};

const std = @import("std");
const mem = std.mem;

pub fn label(allocator: mem.Allocator, colors: []const ColorBand) mem.Allocator.Error![]u8 {
    var value: f64 = @as(f64, @floatFromInt(@intFromEnum(colors[0]) * 10 + @intFromEnum(colors[1]))) *
        std.math.pow(f64, 10.0, @floatFromInt(@intFromEnum(colors[2])));
    var tag: []const u8 = undefined;

    if (value < 1_000) {
        tag = "ohms";
    } else if (value < 1_000_000) {
        value /= 1_000;
        tag = "kiloohms";
    } else if (value < 1_000_000_000) {
        value /= 1_000_000;
        tag = "megaohms";
    } else {
        value /= 1_000_000_000;
        tag = "gigaohms";
    }

    return std.fmt.allocPrint(allocator, "{} {s}", .{ value, tag });
}
