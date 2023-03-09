//
// Created by Cuong Dang on 3/8/23.
//

#ifndef CONCURRENT_DS_ASSERT_MACRO_H
#define CONCURRENT_DS_ASSERT_MACRO_H

#include <stdio.h>


#define assertEqual(...)               \
do {                                            \
    if( !( __VA_ARGS__ ) ) {                     \
        std::cerr << "Unit test assert [ " \
        << ( #__VA_ARGS__ )             \
        << " ] failed in line [ "       \
        << __LINE__                     \
        << " ] file [ "                 \
        << __FILE__ << " ]"             \
        << std::endl;                     \
        err_code = 1;                           \
    }                                            \
} while( false )

#endif //CONCURRENT_DS_ASSERT_MACRO_H
