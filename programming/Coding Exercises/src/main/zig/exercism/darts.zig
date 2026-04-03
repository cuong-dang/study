pub const Coordinate = struct {
    x: f32,
    y: f32,

    pub fn init(x_coord: f32, y_coord: f32) Coordinate {
        return Coordinate{ .x = x_coord, .y = y_coord };
    }
    pub fn score(self: Coordinate) usize {
        const d2 = self.x * self.x + self.y * self.y;
        if (d2 > 100) return 0;
        if (d2 > 25) return 1;
        if (d2 > 1) return 5;
        return 10;
    }
};
