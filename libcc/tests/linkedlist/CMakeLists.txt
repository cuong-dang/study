create_test_sourcelist(cc_linkedlist_tests
    cc_linkedlist_tests.c
    test_cc_linkedlist_new.c
    test_cc_linkedlist_add.c)

add_executable(cc_linkedlist_tests ${cc_linkedlist_tests})
target_link_libraries(cc_linkedlist_tests PUBLIC cc)
target_include_directories(cc_linkedlist_tests PUBLIC
    ${PROJECT_SOURCE_DIR}/src/includes)

set(TestsToRun ${cc_linkedlist_tests})
list(REMOVE_ITEM TestsToRun cc_linkedlist_tests.c)
foreach (test ${TestsToRun})
    get_filename_component(TName ${test} NAME_WE)
    add_test(NAME ${TName} COMMAND cc_linkedlist_tests ${TName})
endforeach ()
