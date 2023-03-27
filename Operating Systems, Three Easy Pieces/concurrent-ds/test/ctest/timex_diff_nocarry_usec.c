#include "assert_macro.h"
#include "../../includes/timex/timex.h"

int timex_diff_nocarry_usec() {
    int err_code = 0;
    struct timeval start = {1, 300000}, end = {2, 700000},
            actual = diff(start, end);
    assertEqual(actual.tv_sec == 1);
    assertEqual(actual.tv_usec == 400000);
    return err_code;
}

int main() {
    return timex_diff_nocarry_usec();
}
