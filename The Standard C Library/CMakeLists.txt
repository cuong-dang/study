cmake_minimum_required(VERSION 4.0)

project(TheStandardCLibrary C)
set(CMAKE_C_STANDARD 90)
set(CMAKE_C_STANDARD_REQUIRED ON)
set(CMAKE_C_EXTENSIONS OFF)

enable_testing()
add_subdirectory(exercises)
add_subdirectory(tests)

add_compile_options(
        -Wall
        -Wextra
        -Wpedantic

        -Wconversion
        -Wformat=2
        -Wmissing-prototypes
        -Wnull-dereference
        -Wold-style-definition
        -Wshadow
        -Wsign-conversion
        -Wstrict-prototypes
        -Wunreachable-code
)
