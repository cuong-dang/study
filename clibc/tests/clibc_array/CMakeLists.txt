create_test_sourcelist(clibc_array_tests
    clibc_array_tests.c
    test_clibc_array_make.c)

add_executable(clibc_array_tests ${clibc_array_tests})
target_link_libraries(clibc_array_tests PUBLIC clibc_array)
target_include_directories(clibc_array_tests PUBLIC
    ${PROJECT_SOURCE_DIR}/src/includes)

set(TestsToRun ${clibc_array_tests})
list(REMOVE_ITEM TestsToRun clibc_array_tests.c)
foreach (test ${TestsToRun})
    get_filename_component(TName ${test} NAME_WE)
    add_test(NAME ${TName} COMMAND clibc_array_tests ${TName})
endforeach ()
