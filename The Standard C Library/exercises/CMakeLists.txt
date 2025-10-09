# Collect all exercise source files
file(GLOB EXERCISE_SOURCES "*.c")

# Create a library for exercises
add_library(exercises STATIC ${EXERCISE_SOURCES})

# Add the include directory so both the library and tests can find headers
target_include_directories(exercises PUBLIC ${CMAKE_SOURCE_DIR}/include)
