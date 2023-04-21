create_test_sourcelist(glinkedlist_tests
        glinkedlist_tests.c
        test_glinkedlist_new.c
        test_glinkedlist_add.c
        test_glinkedlist_get.c)

add_executable(glinkedlist_tests ${glinkedlist_tests})
target_link_libraries(glinkedlist_tests PUBLIC glinkedlist)
target_include_directories(glinkedlist_tests PUBLIC
        "${PROJECT_SOURCE_DIR}/src/includes"
        "${PROJECT_SOURCE_DIR}/test")

set(TestsToRun ${glinkedlist_tests})
list(REMOVE_ITEM TestsToRun glinkedlist_tests.c)
foreach (test ${TestsToRun})
    get_filename_component(TName ${test} NAME_WE)
    add_test(NAME ${TName} COMMAND glinkedlist_tests ${TName})
endforeach ()
