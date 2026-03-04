const std = @import("std");

const tab: *const [64]u8 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";

pub fn encode(bytes: []const u8, alloc: std.mem.Allocator) ![]u8 {
    const n = (bytes.len * 8 + 5) / 6 + 1; // ceil + 1
    const res = try alloc.alloc(u8, n);
    var n_newbits: i32 = 6;
    var i: u8 = 0;
    var last: u8 = 0;
    var done = false;
    var ri: u8 = 0;
    while (!done) {
        var byte: u8 = undefined;
        var idx: usize = 0;
        if (n_newbits != 0) { // need a new byte
            const l_shift: u3 = @intCast(n_newbits);
            idx = last << l_shift;
            if (i == bytes.len) { // no more new bytes
                done = true;
            } else {
                byte = bytes[i];
                i += 1;
                // construct the msb
                const r_shift: u3 = @intCast(8 - n_newbits);
                idx |= byte >> r_shift;
                last = (byte << l_shift) >> l_shift;
            }
        } else {
            idx = last;
        }
        n_newbits = if (n_newbits == 0) 6 else n_newbits - 2;
        res[ri] = tab[idx];
        ri += 1;
    }
    res[ri] = '=';
    return res;
}
