create_test_sourcelist(zem_tests
        zem_tests.c
        zem.c)

add_executable(zem_tests ${zem_tests})
target_link_libraries(zem_tests PUBLIC zem)
target_include_directories(zem_tests PUBLIC
        "${PROJECT_SOURCE_DIR}/src/includes"
        "${PROJECT_SOURCE_DIR}/test")

set(TestsToRun ${zem_tests})
list(REMOVE_ITEM TestsToRun zem_tests.c)
foreach (test ${TestsToRun})
    get_filename_component(TName ${test} NAME_WE)
    add_test(NAME ${TName} COMMAND zem_tests ${TName})
endforeach ()
