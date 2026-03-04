const std = @import("std");
const base64 = @import("base64.zig");

pub fn main() !void {
    var gpa = std.heap.GeneralPurposeAllocator(.{}){};
    const out = try base64.encode("Hi", gpa.allocator());
    defer gpa.free(out);
    std.debug.print("{any}\n", .{out});
}
