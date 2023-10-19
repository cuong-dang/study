create_test_sourcelist(clibc_linkedlist_tests
    clibc_linkedlist_tests.c
    test_clibc_linkedlist_new.c
    test_clibc_linkedlist_add.c)

add_executable(clibc_linkedlist_tests ${clibc_linkedlist_tests})
target_link_libraries(clibc_linkedlist_tests PUBLIC clibc)
target_include_directories(clibc_linkedlist_tests PUBLIC
    ${PROJECT_SOURCE_DIR}/src/includes)

set(TestsToRun ${clibc_linkedlist_tests})
list(REMOVE_ITEM TestsToRun clibc_linkedlist_tests.c)
foreach (test ${TestsToRun})
    get_filename_component(TName ${test} NAME_WE)
    add_test(NAME ${TName} COMMAND clibc_linkedlist_tests ${TName})
endforeach ()
