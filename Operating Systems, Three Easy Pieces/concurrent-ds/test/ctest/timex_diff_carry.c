#include "assert_macro.h"
#include "../../includes/timex/timex.h"

int timex_diff_carry() {
    int err_code = 0;
    struct timeval start = {1, 700000}, end = {2, 300000},
            actual = diff(start, end);
    assertEqual(actual.tv_sec == 0);
    assertEqual(actual.tv_usec == 600000);
    return err_code;
}

int main() {
    return timex_diff_carry();
}

