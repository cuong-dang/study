create_test_sourcelist(cc_graph_tests
    cc_graph_tests.c
    test_cc_graph_new.c
    test_cc_graph_vert_new.c
    test_cc_graph_edge_new.c
    test_cc_graph_adj_verts.c
    test_cc_graph_incd_edges.c)

add_executable(cc_graph_tests ${cc_graph_tests})
target_link_libraries(cc_graph_tests PUBLIC cc)
target_include_directories(cc_graph_tests PUBLIC
    ${PROJECT_SOURCE_DIR}/src/includes)

set(TestsToRun ${cc_graph_tests})
list(REMOVE_ITEM TestsToRun cc_graph_tests.c)
foreach (test ${TestsToRun})
    get_filename_component(TName ${test} NAME_WE)
    add_test(NAME ${TName} COMMAND cc_graph_tests ${TName})
endforeach ()
