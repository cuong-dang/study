create_test_sourcelist(clibc_map_tests
    clibc_map_tests.c
    test_clibc_map_new.c
    test_clibc_map_put.c
    test_clibc_map_get.c)

add_executable(clibc_map_tests ${clibc_map_tests})
target_link_libraries(clibc_map_tests PUBLIC clibc)
target_include_directories(clibc_map_tests PUBLIC
    ${PROJECT_SOURCE_DIR}/src/includes)

set(TestsToRun ${clibc_map_tests})
list(REMOVE_ITEM TestsToRun clibc_map_tests.c)
foreach (test ${TestsToRun})
    get_filename_component(TName ${test} NAME_WE)
    add_test(NAME ${TName} COMMAND clibc_map_tests ${TName})
endforeach ()
