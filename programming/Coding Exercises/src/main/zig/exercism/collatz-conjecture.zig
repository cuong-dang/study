// Please implement the `ComputationError.IllegalArgument` error.

pub const ComputationError = error{
    IllegalArgument,
};

pub fn steps(number: usize) anyerror!usize {
    if (number == 0) return ComputationError.IllegalArgument;
    var n: usize = number;
    var num_steps: usize = 0;
    while (n != 1) {
        num_steps += 1;
        n = if (n % 2 == 0) n / 2 else n * 3 + 1;
    }
    return num_steps;
}
