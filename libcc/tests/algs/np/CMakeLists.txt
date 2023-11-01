create_test_sourcelist(cc_algs_np_tests
    cc_algs_np_tests.c
    test_cc_algs_np_nqueens.c
    test_cc_algs_np_subset_sum.c)

add_executable(cc_algs_np_tests ${cc_algs_np_tests})
target_link_libraries(cc_algs_np_tests PUBLIC cc)
target_include_directories(cc_algs_np_tests PUBLIC
    ${PROJECT_SOURCE_DIR}/src/includes)

set(TestsToRun ${cc_algs_np_tests})
list(REMOVE_ITEM TestsToRun cc_algs_np_tests.c)
foreach (test ${TestsToRun})
    get_filename_component(TName ${test} NAME_WE)
    add_test(NAME ${TName} COMMAND cc_algs_np_tests ${TName})
endforeach ()
