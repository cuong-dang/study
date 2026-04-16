const std = @import("std");
const mem = std.mem;

/// Returns the set of strings in `candidates` that are anagrams of `word`.
/// Caller owns the returned memory.
pub fn detectAnagrams(
    allocator: mem.Allocator,
    word: []const u8,
    candidates: []const []const u8,
) !std.BufSet {
    var res = std.BufSet.init(allocator);
    errdefer res.deinit();

    for (candidates) |candidate| {
        if (isAnagram(word, candidate)) try res.insert(candidate);
    }

    return res;
}

fn isAnagram(word: []const u8, candidate: []const u8) bool {
    if (word.len != candidate.len) return false;

    var same = true;
    var count = [_]isize{0} ** 26;

    for (0..word.len) |i| {
        const wc = std.ascii.toLower(word[i]);
        const cc = std.ascii.toLower(candidate[i]);

        if (wc != cc) same = false;
        count[cc - 'a'] += 1;
        count[wc - 'a'] -= 1;
    }

    if (same) return false;

    return for (0..26) |i| {
        if (count[i] != 0) break false;
    } else true;
}
