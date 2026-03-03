create_test_sourcelist(timex_tests
        timex_tests.c
        timex_diff_nocarry_sec.c
        timex_diff_nocarry_usec.c
        timex_diff_carry.c)

add_executable(timex_tests ${timex_tests})
target_link_libraries(timex_tests PUBLIC timex)
target_include_directories(timex_tests PUBLIC
        "${PROJECT_SOURCE_DIR}/src/includes"
        "${PROJECT_SOURCE_DIR}/test")

set(TestsToRun ${timex_tests})
list(REMOVE_ITEM TestsToRun timex_tests.c)
foreach (test ${TestsToRun})
    get_filename_component(TName ${test} NAME_WE)
    add_test(NAME ${TName} COMMAND timex_tests ${TName})
endforeach ()
