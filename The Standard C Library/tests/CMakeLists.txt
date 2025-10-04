# Collect all test source files
file(GLOB TEST_SOURCES "test_*.c")

# For each test file, create an executable and link with exercises_lib
foreach (test_file ${TEST_SOURCES})
    get_filename_component(test_name ${test_file} NAME_WE)
    add_executable(${test_name} ${test_file})
    target_link_libraries(${test_name} PRIVATE exercises)

    # Register with CTest
    add_test(NAME ${test_name} COMMAND ${test_name})
endforeach ()
