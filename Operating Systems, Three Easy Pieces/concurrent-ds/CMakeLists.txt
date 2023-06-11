cmake_minimum_required(VERSION 3.24)
project(concurrent_ds C)
set(CMAKE_C_STANDARD 99)

add_subdirectory(src/lib/timex)
add_subdirectory(src/lib/ccounter)
add_subdirectory(src/lib/acounter)
add_subdirectory(src/lib/glinkedlist)
add_subdirectory(src/lib/util)
add_subdirectory(src/lib/zem)

add_executable(q2 src/q2.c)
target_link_libraries(q2 PUBLIC timex ccounter)
target_include_directories(q2 PUBLIC "${PROJECT_SOURCE_DIR}/src/includes")

add_executable(q3 src/q3.c)
target_link_libraries(q3 PUBLIC timex acounter)
target_include_directories(q2 PUBLIC "${PROJECT_SOURCE_DIR}/src/includes")

add_executable(q4 src/q4.c)
target_link_libraries(q4 PUBLIC timex glinkedlist util)
target_include_directories(q2 PUBLIC "${PROJECT_SOURCE_DIR}/src/includes")

add_executable(ch31_1 src/ch31_1.c)
target_link_libraries(ch31_1 PUBLIC zem)
target_include_directories(ch31_1 PUBLIC "${PROJECT_SOURCE_DIR}/src/includes")

add_executable(ch31_2 src/ch31_2.c)
target_link_libraries(ch31_2 PUBLIC zem)
target_include_directories(ch31_2 PUBLIC "${PROJECT_SOURCE_DIR}/src/includes")

add_executable(ch31_3 src/ch31_3.c)
target_link_libraries(ch31_3 PUBLIC zem)
target_include_directories(ch31_3 PUBLIC "${PROJECT_SOURCE_DIR}/src/includes")

add_executable(ch31_4 src/ch31_4.c)
target_link_libraries(ch31_4 PUBLIC zem)
target_include_directories(ch31_4 PUBLIC "${PROJECT_SOURCE_DIR}/src/includes")

add_executable(ch31_5_1 src/ch31_5_1.c)
target_link_libraries(ch31_5_1 PUBLIC zem)
target_include_directories(ch31_5_1 PUBLIC "${PROJECT_SOURCE_DIR}/src/includes")

enable_testing()
add_subdirectory(test)
