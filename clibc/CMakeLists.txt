cmake_minimum_required(VERSION 3.25)
project(clibc C)
set(CMAKE_C_STANDARD 90)

add_library(clibc_array src/clibc_array.c)
target_include_directories(clibc_array PUBLIC src/includes)

enable_testing()
add_subdirectory(tests)
