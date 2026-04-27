const std = @import("std");

pub fn response(s: []const u8) []const u8 {
    const trimmed = std.mem.trim(u8, s, &std.ascii.whitespace);
    if (trimmed.len == 0) return "Fine. Be that way!";
    if (trimmed[trimmed.len - 1] == '?') {
        if (isAllCap(trimmed)) return "Calm down, I know what I'm doing!";
        return "Sure.";
    }
    if (isAllCap(trimmed)) return "Whoa, chill out!";
    return "Whatever.";
}

fn isAllCap(s: []const u8) bool {
    var hasLetters = false;
    for (s) |c| {
        if (std.ascii.isLower(c)) return false;
        if (std.ascii.isAlphabetic(c)) hasLetters = true;
    }
    return hasLetters;
}
