create_test_sourcelist(clibc_graph_tests
    clibc_graph_tests.c
    test_clibc_graph_new.c
    test_clibc_graph_vert_new.c
    test_clibc_graph_edge_new.c)

add_executable(clibc_graph_tests ${clibc_graph_tests})
target_link_libraries(clibc_graph_tests PUBLIC clibc)
target_include_directories(clibc_graph_tests PUBLIC
    ${PROJECT_SOURCE_DIR}/src/includes)

set(TestsToRun ${clibc_graph_tests})
list(REMOVE_ITEM TestsToRun clibc_graph_tests.c)
foreach (test ${TestsToRun})
    get_filename_component(TName ${test} NAME_WE)
    add_test(NAME ${TName} COMMAND clibc_graph_tests ${TName})
endforeach ()
