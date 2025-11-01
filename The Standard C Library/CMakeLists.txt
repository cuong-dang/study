cmake_minimum_required(VERSION 3.10)
project(stdc VERSION 0.0.1 LANGUAGES C)
set(CMAKE_C_STANDARD 90)
set(CMAKE_C_STANDARD_REQUIRED True)
set(CMAKE_C_EXTENSIONS OFF)

# sources
file(GLOB_RECURSE SOURCES CONFIGURE_DEPENDS
  ${CMAKE_CURRENT_SOURCE_DIR}/src/*.c
)
add_library(exercises OBJECT ${SOURCES})
target_include_directories(exercises PUBLIC ${CMAKE_SOURCE_DIR}/include)

# link libcc
include_directories("/usr/local/include")
link_directories("/usr/local/lib")
target_link_libraries(exercises PRIVATE cc)

# tests
include(CTest)
if (BUILD_TESTING)
  add_subdirectory(tests)
endif ()
