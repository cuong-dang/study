cmake_minimum_required(VERSION 3.22)

add_executable(timex_diff_nocarry_sec timex_diff_nocarry_sec.c ../../includes/timex/timex.c)
add_executable(timex_diff_nocarry_usec timex_diff_nocarry_usec.c ../../includes/timex/timex.c)
add_executable(timex_diff_carry timex_diff_carry.c ../../includes/timex/timex.c)
add_test(timex_diff_nocarry_sec timex_diff_nocarry_sec)
add_test(timex_diff_nocarry_usec timex_diff_nocarry_usec)
add_test(timex_diff_carry timex_diff_carry)
