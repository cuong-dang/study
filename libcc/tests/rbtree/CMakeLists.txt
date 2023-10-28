create_test_sourcelist(cc_rbtree_tests
    cc_rbtree_tests.c
    test_cc_rbtree_new.c
    test_cc_rbtree_add.c
    test_cc_rbtree_get.c
    test_cc_rbtree_rm_min.c
    test_cc_rbtree_keys.c)

add_executable(cc_rbtree_tests ${cc_rbtree_tests})
target_link_libraries(cc_rbtree_tests PUBLIC cc)
target_include_directories(cc_rbtree_tests PUBLIC
    ${PROJECT_SOURCE_DIR}/src/includes)

set(TestsToRun ${cc_rbtree_tests})
list(REMOVE_ITEM TestsToRun cc_rbtree_tests.c)
foreach (test ${TestsToRun})
    get_filename_component(TName ${test} NAME_WE)
    add_test(NAME ${TName} COMMAND cc_rbtree_tests ${TName})
endforeach ()
