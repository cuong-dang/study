const std = @import("std");
const mem = std.mem;

pub fn abbreviate(allocator: mem.Allocator, words: []const u8) mem.Allocator.Error![]u8 {
    var res = std.ArrayList(u8){};
    errdefer res.deinit(allocator);
    var add_next = true;
    for (words) |c| {
        if (add_next and std.ascii.isAlphabetic(c)) {
            try res.append(allocator, std.ascii.toUpper(c));
            add_next = false;
        } else if (c == ' ' or c == '-') {
                add_next = true;
            }
        }
    return try res.toOwnedSlice(allocator);
}

test {
    const allocator = std.testing.allocator;
    const actual = try abbreviate(std.testing.allocator, "Hello, world");
    defer allocator.free(actual);
    try std.testing.expectEqualSlices(u8, "HW", actual);
}
