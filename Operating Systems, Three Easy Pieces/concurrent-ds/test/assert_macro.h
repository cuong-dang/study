//
// Created by Cuong Dang on 3/8/23.
//

#ifndef CONCURRENT_DS_ASSERT_MACRO_H
#define CONCURRENT_DS_ASSERT_MACRO_H

#include <stdio.h>

#define assertEqual(...)                         \
do {                                             \
    if( !( __VA_ARGS__ ) ) {                     \
        fprintf(stderr, "Unit test assert [ ");  \
        fprintf(stderr, #__VA_ARGS__);           \
        fprintf(stderr, " ] failed in line [ "); \
        fprintf(stderr, "%d", __LINE__);         \
        fprintf(stderr, " ] file [ ");           \
        fprintf(stderr, __FILE__);               \
        fprintf(stderr, " ]\n");                 \
        err_code = 1;                            \
    }                                            \
} while( 0 )

#endif //CONCURRENT_DS_ASSERT_MACRO_H
