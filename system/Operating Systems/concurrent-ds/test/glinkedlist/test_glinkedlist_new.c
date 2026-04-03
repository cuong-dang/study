#include "assert_macro.h"
#include "glinkedlist.h"

int test_glinkedlist_new_with_lockstrat(int lockstrat);

int test_glinkedlist_new(int argc, char **argv)
{
    return test_glinkedlist_new_with_lockstrat(GLINKEDLIST_LOCKSTRAT_GLOBAL) ||
           test_glinkedlist_new_with_lockstrat(GLINKEDLIST_LOCKSTRAT_HOH);
}

int test_glinkedlist_new_with_lockstrat(int lockstrat)
{
    int err_code = 0;
    glinkedlist_t *g = glinkedlist_new(sizeof(char), sizeof(int), lockstrat);

    assertEqual(g->head == NULL);
    assertEqual(g->keysize == sizeof(char));
    assertEqual(g->valsize == sizeof(int));
    assertEqual(g->n == 0);
    return err_code;
}
