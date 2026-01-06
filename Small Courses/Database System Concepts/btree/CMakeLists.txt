set(CMAKE_OSX_ARCHITECTURES "x86_64")

cmake_minimum_required(VERSION 3.22)
project(btree C)

set(CMAKE_C_STANDARD 90)

add_executable(btree main.c)

find_package(PkgConfig REQUIRED)
pkg_check_modules(deps REQUIRED IMPORTED_TARGET glib-2.0)
target_link_libraries(btree PkgConfig::deps)
