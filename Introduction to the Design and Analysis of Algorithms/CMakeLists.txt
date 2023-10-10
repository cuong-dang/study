cmake_minimum_required(VERSION 3.25)
project(idaa C)
set(CMAKE_C_STANDARD 90)

add_subdirectory(src/ch1)
add_subdirectory(src/ch2)
add_subdirectory(src/ch5)

enable_testing()
add_subdirectory(tests)
