const std = @import("std");
const mem = std.mem;

pub fn toRna(allocator: mem.Allocator, dna: []const u8) mem.Allocator.Error![]const u8 {
    var res = try allocator.alloc(u8, dna.len);
    for (dna, 0..) |n, i| {
        res[i] = switch (n) {
            'G' => 'C',
            'C' => 'G',
            'T' => 'A',
            'A' => 'U',
            else => unreachable,
        };
    }
    return res;
}
