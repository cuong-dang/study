create_test_sourcelist(cc_set_tests
    cc_set_tests.c
    test_cc_set_new_add_find.c)

add_executable(cc_set_tests ${cc_set_tests})
target_link_libraries(cc_set_tests PUBLIC cc)
target_include_directories(cc_set_tests PUBLIC
    ${PROJECT_SOURCE_DIR}/src/includes)

set(TestsToRun ${cc_set_tests})
list(REMOVE_ITEM TestsToRun cc_set_tests.c)
foreach (test ${TestsToRun})
    get_filename_component(TName ${test} NAME_WE)
    add_test(NAME ${TName} COMMAND cc_set_tests ${TName})
endforeach ()
