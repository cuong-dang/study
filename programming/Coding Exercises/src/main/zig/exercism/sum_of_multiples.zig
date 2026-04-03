const std = @import("std");
const mem = std.mem;

pub fn sum(allocator: mem.Allocator, factors: []const u32, limit: u32) !u64 {
    var res: u64 = 0;
    var seen = try allocator.alloc(bool, limit);
    defer allocator.free(seen);
    @memset(seen, false);

    for (factors) |f| {
        if (f == 0) continue;
        var fm = f;
        while (fm < limit) : (fm += f) {
            if (!seen[fm]) {
                res += fm;
                seen[fm] = true;
            }
        }
    }
    return res;
}
