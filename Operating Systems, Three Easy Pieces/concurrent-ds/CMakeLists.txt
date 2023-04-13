cmake_minimum_required(VERSION 3.24)
project(concurrent_ds C)
set(CMAKE_C_STANDARD 99)

add_subdirectory(src/lib/timex)
add_subdirectory(src/lib/ccounter)
add_subdirectory(src/lib/acounter)

add_executable(q2 src/q2.c)
target_link_libraries(q2 PUBLIC timex ccounter)
target_include_directories(q2 PUBLIC "${PROJECT_SOURCE_DIR}/src/includes")

add_executable(q3 src/q3.c)
target_link_libraries(q3 PUBLIC timex acounter)
target_include_directories(q2 PUBLIC "${PROJECT_SOURCE_DIR}/src/includes")

enable_testing()
add_subdirectory(test)
