create_test_sourcelist(cc_pqueue_tests
    cc_pqueue_tests.c
    test_cc_pqueue_add_rm.c)

add_executable(cc_pqueue_tests ${cc_pqueue_tests})
target_link_libraries(cc_pqueue_tests PUBLIC cc)
target_include_directories(cc_pqueue_tests PUBLIC
    ${PROJECT_SOURCE_DIR}/src/includes)

set(TestsToRun ${cc_pqueue_tests})
list(REMOVE_ITEM TestsToRun cc_pqueue_tests.c)
foreach (test ${TestsToRun})
    get_filename_component(TName ${test} NAME_WE)
    add_test(NAME ${TName} COMMAND cc_pqueue_tests ${TName})
endforeach ()
