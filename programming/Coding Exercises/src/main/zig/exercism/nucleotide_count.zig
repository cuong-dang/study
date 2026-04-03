pub const NucleotideError = error{Invalid};

pub const Counts = struct {
    a: u32,
    c: u32,
    g: u32,
    t: u32,
};

pub fn countNucleotides(s: []const u8) NucleotideError!Counts {
    var res: Counts = .{
        .a = 0,
        .c = 0,
        .g = 0,
        .t = 0,
    };

    for (s) |c| {
        switch (c) {
            'A' => res.a += 1,
            'C' => res.c += 1,
            'G' => res.g += 1,
            'T' => res.t += 1,
            else => return NucleotideError.Invalid,
        }
    }
    return res;
}
