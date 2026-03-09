#include "util.h"

#define DEBUG 0

void printd(char *format, ...)
{
    va_list args;
    va_start(args, format);
    if (DEBUG)
    {
        printf("[DEBUG] ");
        vprintf(format, args);
    }
    va_end(args);
}
