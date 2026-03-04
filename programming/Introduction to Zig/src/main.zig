const std = @import("std");
const base64 = @import("base64.zig");

pub fn main() !void {
    var gpa = std.heap.GeneralPurposeAllocator(.{}){};
    const allocator = gpa.allocator();
    const out = try base64.encode("Testing some more stuff", allocator);
    defer allocator.free(out);
    std.debug.print("{s}\n", .{out});
}
