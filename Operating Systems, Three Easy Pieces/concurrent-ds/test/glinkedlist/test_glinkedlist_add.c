#include "assert_macro.h"
#include "glinkedlist.h"

int test_glinkedlist_add_with_lockstrat(int lockstrat);

int test_glinkedlist_add(int argc, char **argv)
{
    return test_glinkedlist_add_with_lockstrat(GLINKEDLIST_LOCKSTRAT_GLOBAL) ||
           test_glinkedlist_add_with_lockstrat(GLINKEDLIST_LOCKSTRAT_HOH);
}

int test_glinkedlist_add_with_lockstrat(int lockstrat)
{
    int err_code = 0;
    glinkedlist_t *g = glinkedlist_new(sizeof(char), sizeof(int), lockstrat);
    char key;
    int val;

    key = 'a';
    val = 1;
    glinkedlist_add(g, &key, &val);
    assertEqual(*(char *)g->head->key == key);
    assertEqual(*(int *)g->head->val == val);
    assertEqual(g->n == 1);

    key = 'b';
    val = 2;
    glinkedlist_add(g, &key, &val);
    assertEqual(*(char *)g->head->key == key);
    assertEqual(*(int *)g->head->val == val);
    assertEqual(g->n == 2);

    key = 'a';
    val = 3;
    glinkedlist_add(g, &key, &val);
    assertEqual(*(char *)g->head->key == key);
    assertEqual(*(int *)g->head->val == val);
    assertEqual(g->n == 3);
    return err_code;
}
