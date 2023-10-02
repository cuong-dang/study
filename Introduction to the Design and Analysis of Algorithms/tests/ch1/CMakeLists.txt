create_test_sourcelist(ch1_tests
        ch1_tests.c
        test_ex114.c
        test_ex115.c)

add_executable(ch1_tests ${ch1_tests})
target_link_libraries(ch1_tests PUBLIC ch1)
target_include_directories(ch1_tests PUBLIC
        "${PROJECT_SOURCE_DIR}/src/includes"
        "${PROJECT_SOURCE_DIR}/tests")

set(TestsToRun ${ch1_tests})
list(REMOVE_ITEM TestsToRun ch1_tests.c)
foreach (test ${TestsToRun})
    get_filename_component(TName ${test} NAME_WE)
    add_test(NAME ${TName} COMMAND ch1_tests ${TName})
endforeach ()
