cmake_minimum_required(VERSION 3.25)
project(cc C)
set(CMAKE_C_STANDARD 90)

add_library(cc
    src/array.c
    src/linkedlist.c
    src/rbtree.c
    src/set.c
    src/pqueue.c
    src/graph.c
    src/algs/graph.c
    src/algs/str.c
    src/algs/np.c)
target_include_directories(cc PUBLIC src/includes)

enable_testing()
add_subdirectory(tests)
