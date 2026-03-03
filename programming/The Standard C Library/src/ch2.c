#include <cc/std/ctype.h>
#include <stddef.h>

size_t idlen(const char *s) {
    int count = 0;

    for (; *s; ++s) {
        if (count == 0 && !isalpha(*s)) {
            return count;
        }
        if (!isalpha(*s) && !isdigit(*s) && *s != '_') {
            return count;
        }
        ++count;
    }
    return count;
}
