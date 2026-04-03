pub const Planet = enum(u32) {
    mercury,
    venus,
    earth,
    mars,
    jupiter,
    saturn,
    uranus,
    neptune,

    const earth_secs = 31_557_600;
    const ratios = [_]f64{ 0.2408467, 0.61519726, 1.0, 1.8808158, 11.862615, 29.447498, 84.016846, 164.79132 };

    pub fn age(self: Planet, seconds: usize) f64 {
        return @as(f64, @floatFromInt(seconds)) / earth_secs / ratios[@intFromEnum(self)];
    }
};
