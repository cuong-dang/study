cmake_minimum_required(VERSION 3.25)
project(clibc C)
set(CMAKE_C_STANDARD 90)

add_library(clibc
    src/clibc_array.c
    src/clibc_linkedlist.c
    src/clibc_map.c)
target_include_directories(clibc PUBLIC src/includes)

enable_testing()
add_subdirectory(tests)
