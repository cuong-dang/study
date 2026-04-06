const std = @import("std");
const mem = std.mem;

pub fn transform(allocator: mem.Allocator, legacy: std.AutoHashMap(i5, []const u8)) mem.Allocator.Error!std.AutoHashMap(u8, i5) {
    var new = std.AutoHashMap(u8, i5).init(allocator);
    errdefer new.deinit();
    var it = legacy.iterator();
    while (it.next()) |entry| {
        const point = entry.key_ptr.*;
        const letters = entry.value_ptr.*;
        for (letters) |letter| {
            try new.put(std.ascii.toLower(letter), point);
        }
    }
    return new;
}
