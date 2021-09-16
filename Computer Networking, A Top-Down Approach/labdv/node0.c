#include <stdio.h>

#include "core.h"

extern int TRACE;
extern int YES;
extern int NO;

struct distance_table dt0;

/* students to write the following two routines, and maybe some others */
void printdt0(struct distance_table *dt0);

int isconnected0[4] = {0, 1, 1, 1};

void rtinit0()

{
  rinit(0, &dt0, 0, 1, 3, 7, isconnected0, printdt0);
}

void rtupdate0(rcvdpkt)
  struct rtpkt *rcvdpkt;

{
  rtupdate(0, &dt0, rcvdpkt, isconnected0, printdt0);
}

void printdt0(dtptr)
  struct distance_table *dtptr;
{
  printf("                via     \n");
  printf("   D0 |    1     2    3 \n");
  printf("  ----|-----------------\n");
  printf("     1|  %3d   %3d   %3d\n",dtptr->costs[1][1],
         dtptr->costs[1][2],dtptr->costs[1][3]);
  printf("dest 2|  %3d   %3d   %3d\n",dtptr->costs[2][1],
         dtptr->costs[2][2],dtptr->costs[2][3]);
  printf("     3|  %3d   %3d   %3d\n",dtptr->costs[3][1],
         dtptr->costs[3][2],dtptr->costs[3][3]);
}

linkhandler0(linkid, newcost)
  int linkid, newcost;

/* called when cost from 0 to linkid changes from current value to newcost*/
/* You can leave this routine empty if you're an undergrad. If you want */
/* to use this routine, you'll need to change the value of the LINKCHANGE */
/* constant definition in prog3.c from 0 to 1 */

{
}
