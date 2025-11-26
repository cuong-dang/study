cmake_minimum_required(VERSION 3.28)
project(aic LANGUAGES C)
set(CMAKE_C_STANDARD 90)

# Chapter 1
add_executable(prog1_1)
target_sources(prog1_1 PRIVATE src/ch1/prog1_1.c)
