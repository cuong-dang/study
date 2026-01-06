#include "assert_macro.h"
#include "glinkedlist.h"

int test_glinkedlist_get_with_lockstrat(int lockstrat);

int test_glinkedlist_get(int argc, char **argv)
{
    return test_glinkedlist_get_with_lockstrat(GLINKEDLIST_LOCKSTRAT_GLOBAL) ||
           test_glinkedlist_get_with_lockstrat(GLINKEDLIST_LOCKSTRAT_HOH);
}

int test_glinkedlist_get_with_lockstrat(int lockstrat)
{
    int err_code = 0;
    glinkedlist_t *g = glinkedlist_new(sizeof(char), sizeof(int), lockstrat);
    char key;
    int val;

    key = 'a';
    assertEqual(glinkedlist_get(g, &key) == NULL);
    val = 1;
    glinkedlist_add(g, &key, &val);
    assertEqual(*(int *)glinkedlist_get(g, &key) == val);
    key = 'b';
    assertEqual(glinkedlist_get(g, &key) == NULL);

    val = 2;
    glinkedlist_add(g, &key, &val);
    assertEqual(*(int *)glinkedlist_get(g, &key) == val);

    val = 3;
    glinkedlist_add(g, &key, &val);
    assertEqual(*(int *)glinkedlist_get(g, &key) == val);
    return err_code;
}
