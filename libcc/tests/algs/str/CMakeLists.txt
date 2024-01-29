create_test_sourcelist(cc_algs_str_tests
    cc_algs_str_tests.c
    test_cc_algs_str_horspool_match.c)

add_executable(cc_algs_str_tests ${cc_algs_str_tests})
target_link_libraries(cc_algs_str_tests PUBLIC cc)
target_include_directories(cc_algs_str_tests PUBLIC
    ${PROJECT_SOURCE_DIR}/src/includes)

set(TestsToRun ${cc_algs_str_tests})
list(REMOVE_ITEM TestsToRun cc_algs_str_tests.c)
foreach (test ${TestsToRun})
    get_filename_component(TName ${test} NAME_WE)
    add_test(NAME ${TName} COMMAND cc_algs_str_tests ${TName})
endforeach ()
