cmake_minimum_required(VERSION 3.25)
project(idaa C)
set(CMAKE_C_STANDARD 90)

add_library(common src/common.c)
target_include_directories(common PUBLIC "${PROJECT_SOURCE_DIR}/src/includes")

add_subdirectory(src/ch1)
add_subdirectory(src/ch2)
add_subdirectory(src/ch5)
