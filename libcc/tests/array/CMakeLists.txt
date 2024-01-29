create_test_sourcelist(cc_array_tests
    cc_array_tests.c
    test_cc_array_new.c
    test_cc_array_add_get.c
    test_cc_array_swap.c
    test_cc_array_rm.c)

add_executable(cc_array_tests ${cc_array_tests})
target_link_libraries(cc_array_tests PUBLIC cc)
target_include_directories(cc_array_tests PUBLIC
    ${PROJECT_SOURCE_DIR}/src/includes)

set(TestsToRun ${cc_array_tests})
list(REMOVE_ITEM TestsToRun cc_array_tests.c)
foreach (test ${TestsToRun})
    get_filename_component(TName ${test} NAME_WE)
    add_test(NAME ${TName} COMMAND cc_array_tests ${TName})
endforeach ()
