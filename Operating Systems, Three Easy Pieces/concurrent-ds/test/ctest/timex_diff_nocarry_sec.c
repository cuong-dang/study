#include "assert_macro.h"
#include "../../includes/timex/timex.h"

int timex_diff_carry() {
    int err_code = 0;
    struct timeval start = {1, 0}, end = {2, 0}, actual = diff(start, end);
    assertEqual(actual.tv_sec == 1);
    assertEqual(actual.tv_usec == 0);
    return err_code;
}

int main() {
    return timex_diff_carry();
}

