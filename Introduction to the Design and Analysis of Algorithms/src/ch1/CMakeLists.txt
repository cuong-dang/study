add_library(ch1
        ex1.1.4.c
        ex1.1.5.c)
target_include_directories(ch1 PUBLIC "${PROJECT_SOURCE_DIR}/src/includes")

add_executable(ex116 ex1.1.6.c)
