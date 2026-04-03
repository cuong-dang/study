const std = @import("std");

const tab: *const [64]u8 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";

pub fn encode(bytes: []const u8, alloc: std.mem.Allocator) ![]const u8 {
    if (bytes.len == 0) return "";

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

pub fn decode(cs: []const u8, allocator: std.mem.Allocator) ![]const u8 {
    if (cs.len == 0) return "";
    if (cs.len % 4 != 0) return error.InvalidBase64;

    var padding: usize = 0;
    if (cs[cs.len - 1] == '=') padding += 1;
    if (cs[cs.len - 2] == '=') padding += 1;
    const out = try allocator.alloc(u8, (cs.len / 4) * 3 - padding);
    errdefer allocator.free(out);
    const blocks = cs.len / 4;
    var i: usize = 0;
    var j: usize = 0;

    for (0..blocks - 1) |_| {
        const idx0 = try indexOf(cs[i]);
        const idx1 = try indexOf(cs[i + 1]);
        const idx2 = try indexOf(cs[i + 2]);
        const idx3 = try indexOf(cs[i + 3]);

        out[j] = (idx0 << 2) | (idx1 >> 4);
        out[j + 1] = ((idx1 & 0xf) << 4) | ((idx2 & 0x3c) >> 2);
        out[j + 2] = ((idx2 & 0x3) << 6) | idx3;

        i += 4;
        j += 3;
    }
    // last block
    const idx0 = try indexOf(cs[i]);
    const idx1 = try indexOf(cs[i + 1]);

    out[j] = (idx0 << 2) | (idx1 >> 4);
    if (padding < 2) {
        const idx2 = try indexOf(cs[i + 2]);
        out[j + 1] = ((idx1 & 0xf) << 4) | ((idx2 & 0x3c) >> 2);
        if (padding == 0) {
            const idx3 = try indexOf(cs[i + 3]);
            out[j + 2] = ((idx2 & 0x3) << 6) | idx3;
        }
    }
    return out;
}

fn indexOf(c: u8) !u8 {
    return switch (c) {
        'A'...'Z' => c - 'A',
        'a'...'z' => c - 'a' + 26,
        '0'...'9' => c - '0' + 52,
        '+' => 62,
        '/' => 63,
        else => error.InvalidBase64,
    };
}

test encode {
    const out = try encode("Many hands make light work.", std.testing.allocator);
    defer std.testing.allocator.free(out);

    try std.testing.expectEqualSlices(u8, "TWFueSBoYW5kcyBtYWtlIGxpZ2h0IHdvcmsu", out);
}

test "encode: no padding needed" {
    const out = try encode("Man", std.testing.allocator);
    defer std.testing.allocator.free(out);

    try std.testing.expectEqualSlices(u8, "TWFu", out);
}

test "encode: one padding character" {
    const out = try encode("Ma", std.testing.allocator);
    defer std.testing.allocator.free(out);

    try std.testing.expectEqualSlices(u8, "TWE=", out);
}

test "encode: two padding characters" {
    const out = try encode("M", std.testing.allocator);
    defer std.testing.allocator.free(out);

    try std.testing.expectEqualSlices(u8, "TQ==", out);
}

test "encode: empty" {
    const out = try encode("", std.testing.allocator);
    defer std.testing.allocator.free(out);

    try std.testing.expectEqualSlices(u8, "", out);
}

test decode {
    const out = try decode("TWFueSBoYW5kcyBtYWtlIGxpZ2h0IHdvcmsu", std.testing.allocator);
    defer std.testing.allocator.free(out);

    try std.testing.expectEqualSlices(u8, "Many hands make light work.", out);
}

test "decode: no padding needed" {
    const out = try decode("TWFu", std.testing.allocator);
    defer std.testing.allocator.free(out);

    try std.testing.expectEqualSlices(u8, "Man", out);
}

test "decode: one padding character" {
    const out = try decode("TWE=", std.testing.allocator);
    defer std.testing.allocator.free(out);

    try std.testing.expectEqualSlices(u8, "Ma", out);
}

test "decode: two padding characters" {
    const out = try decode("TQ==", std.testing.allocator);
    defer std.testing.allocator.free(out);

    try std.testing.expectEqualSlices(u8, "M", out);
}

test "decode: empty" {
    const out = try decode("", std.testing.allocator);
    defer std.testing.allocator.free(out);

    try std.testing.expectEqualSlices(u8, "", out);
}

test "decode: invalid Base64" {
    try std.testing.expectError(error.InvalidBase64, decode("A", std.testing.allocator));
    try std.testing.expectError(error.InvalidBase64, decode("TWF-", std.testing.allocator));
}
