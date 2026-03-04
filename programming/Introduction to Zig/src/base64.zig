const std = @import("std");

const tab: *const [64]u8 = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" ++
    "abcdefghijklmnopqrstuvwxyz" ++
    "0123456789+/";

pub fn encode(bytes: []const u8, alloc: std.mem.Allocator) !*[]const u8 {
    const n = (bytes.len * 8 + 5) / 6 + 1;
    const res = try alloc.alloc(u8, n);
    var n_newbits: u5 = 6;
    var i: usize = 0;
    var last: usize = 0;
    var done = false;
    while (!done) {
        var byte: u8 = undefined;
        var idx: usize = 0;
        if (n_newbits != 0) { // need a new byte
            idx = last << n_newbits;
            if (i == bytes.len) { // no more new bytes
                done = true;
            } else {
                byte = bytes[i];
                i += 1;
                // construct the msb
                idx |= (byte >> (8 - n_newbits));
                last = (byte << n_newbits) >> n_newbits;
            }
        } else {
            idx = last;
        }
        n_newbits = if (n_newbits == 0) 6 else n_newbits - 2;
        res[i] = tab[idx];
        i += 1;
    }
    return res;
}
