cmake_minimum_required(VERSION 3.22)
project(concurrent_ds C)

set(CMAKE_C_STANDARD 99)

add_executable(concurrent_ds main.c includes/timex.c test/ctest/assert_macro.h)
