#include <stdio.h>

#include "core.h"

extern int TRACE;
extern int YES;
extern int NO;

struct distance_table dt3;

/* students to write the following two routines, and maybe some others */
void printdt3(struct distance_table *dt3);

void rtinit3()
{

  rinit(3, &dt3, 7, 999, 2, 0, printdt3);
}

void rtupdate3(rcvdpkt)
  struct rtpkt *rcvdpkt;

{

  rtupdate(3, &dt3, rcvdpkt, printdt3);
}

void printdt3(dtptr)
  struct distance_table *dtptr;

{
  printf("             via     \n");
  printf("   D3 |    0     2 \n");
  printf("  ----|-----------\n");
  printf("     0|  %3d   %3d\n",dtptr->costs[0][0], dtptr->costs[0][2]);
  printf("dest 1|  %3d   %3d\n",dtptr->costs[1][0], dtptr->costs[1][2]);
  printf("     2|  %3d   %3d\n",dtptr->costs[2][0], dtptr->costs[2][2]);

}
