cmake_minimum_required(VERSION 3.26)
project(idaa C)
set(CMAKE_C_STANDARD 90)

add_subdirectory(src/ch1)
add_subdirectory(src/ch2)

enable_testing()
add_subdirectory(tests)
