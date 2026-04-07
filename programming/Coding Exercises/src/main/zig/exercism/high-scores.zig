pub const HighScores = struct {
    _latest: i32,
    _personalBest: i32,
    _personalTopThree: [3]i32,
    _topLen: usize,

    pub fn init(scores: []const i32) HighScores {
        var _personalBest: i32 = -1;
        var tops: [3]i32 = .{ -1, -1, -1 };

        for (scores) |s| {
            if (s > _personalBest) _personalBest = s;
            if (s > tops[0]) {
                tops[2] = tops[1];
                tops[1] = tops[0];
                tops[0] = s;
            } else if (s > tops[1]) {
                tops[2] = tops[1];
                tops[1] = s;
            } else if (s > tops[2]) {
                tops[2] = s;
            }
        }

        return .{
            ._latest = scores[scores.len - 1],
            ._personalBest = _personalBest,
            ._personalTopThree = tops,
            ._topLen = @min(scores.len, 3),
        };
    }

    pub fn latest(self: *const HighScores) ?i32 {
        return self._latest;
    }

    pub fn personalBest(self: *const HighScores) ?i32 {
        return self._personalBest;
    }

    pub fn personalTopThree(self: *const HighScores) []const i32 {
        return self._personalTopThree[0..self._topLen];
    }
};
