cmake_minimum_required(VERSION 3.10)
project(stdc VERSION 0.0.1 LANGUAGES C)
set(CMAKE_C_STANDARD 90)
set(CMAKE_C_STANDARD_REQUIRED True)
set(CMAKE_C_EXTENSIONS OFF)

# sources
file(GLOB_RECURSE SOURCES CONFIGURE_DEPENDS
  ${CMAKE_CURRENT_SOURCE_DIR}/src/*.c
)
add_library(${PROJECT_NAME} OBJECT ${SOURCES})
target_include_directories(${PROJECT_NAME} PUBLIC ${CMAKE_SOURCE_DIR}/include)

# link libcc
include(FetchContent)
FetchContent_Declare(
  cc
  GIT_REPOSITORY https://github.com/cuong-dang/libcc.git
  GIT_TAG cmake
)
FetchContent_MakeAvailable(cc)
target_link_libraries(${PROJECT_NAME} PRIVATE cc)

# tests
include(CTest)
if (BUILD_TESTING)
  add_subdirectory(tests)
endif ()
