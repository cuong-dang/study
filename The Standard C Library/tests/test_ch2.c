#include "ch2.h"
#include <cc/std/assert.h>
#include <stdlib.h>

void test_idlen(void) {
    assert(idlen("") == 0);
    assert(idlen("x") == 1);
    assert(idlen("_x") == 0);
    assert(idlen("0x") == 0);
    assert(idlen("xy_1z") == 5);
}

int main(void) {
    test_idlen();
    return EXIT_SUCCESS;
}
