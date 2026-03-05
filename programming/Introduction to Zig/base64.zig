const std = @import("std");

const tab: *const [64]u8 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";

pub fn encode(bytes: []const u8, alloc: std.mem.Allocator) ![]const u8 {
    const out = try alloc.alloc(u8, 4 * ((bytes.len + 2) / 3));
    var i: usize = 0;
    var j: usize = 0;

    while (i + 3 <= bytes.len) {
        const b0 = bytes[i];
        const b1 = bytes[i + 1];
        const b2 = bytes[i + 2];

        out[j] = tab[b0 >> 2];
        out[j + 1] = tab[((b0 & 0x03) << 4) | (b1 >> 4)];
        out[j + 2] = tab[((b1 & 0x0f) << 2) | (b2 >> 6)];
        out[j + 3] = tab[b2 & 0x3f];

        i += 3;
        j += 4;
    }

    const rem = bytes.len - i;

    if (rem == 1) {
        const b0 = bytes[i];

        out[j] = tab[b0 >> 2];
        out[j + 1] = tab[(b0 & 0x03) << 4];
        out[j + 2] = '=';
        out[j + 3] = '=';
    } else if (rem == 2) {
        const b0 = bytes[i];
        const b1 = bytes[i + 1];

        out[j] = tab[b0 >> 2];
        out[j + 1] = tab[((b0 & 0x03) << 4) | (b1 >> 4)];
        out[j + 2] = tab[(b1 & 0x0f) << 2];
        out[j + 3] = '=';
    }

    return out;
}

test "no padding needed" {
    const out = try encode("Many hands make light work.", std.testing.allocator);
    defer std.testing.allocator.free(out);

    try std.testing.expectEqualSlices(u8, "TWFueSBoYW5kcyBtYWtlIGxpZ2h0IHdvcmsu", out);
}

test "one padding character" {
    const out = try encode("Ma", std.testing.allocator);
    defer std.testing.allocator.free(out);

    try std.testing.expectEqualSlices(u8, "TWE=", out);
}

test "two padding characters" {
    const out = try encode("M", std.testing.allocator);
    defer std.testing.allocator.free(out);

    try std.testing.expectEqualSlices(u8, "TQ==", out);
}
