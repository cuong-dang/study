#include <stdio.h>

#include "core.h"

extern int TRACE;
extern int YES;
extern int NO;

int connectcosts1[4] = { 1,  0,  1, 999 };

struct distance_table dt1;

/* students to write the following two routines, and maybe some others */
void printdt1(struct distance_table *dt1);

int isconnected1[4] = {1, 0, 1, 0};

void rtinit1()
{

  rinit(1, &dt1, 1, 0, 1, 999, isconnected1, printdt1);
}

void rtupdate1(rcvdpkt)
  struct rtpkt *rcvdpkt;

{

  rtupdate(1, &dt1, rcvdpkt, isconnected1, printdt1);
}

void printdt1(dtptr)
  struct distance_table *dtptr;

{
  printf("             via   \n");
  printf("   D1 |    0     2 \n");
  printf("  ----|-----------\n");
  printf("     0|  %3d   %3d\n",dtptr->costs[0][0], dtptr->costs[0][2]);
  printf("dest 2|  %3d   %3d\n",dtptr->costs[2][0], dtptr->costs[2][2]);
  printf("     3|  %3d   %3d\n",dtptr->costs[3][0], dtptr->costs[3][2]);

}

linkhandler1(linkid, newcost)
int linkid, newcost;
/* called when cost from 1 to linkid changes from current value to newcost*/
/* You can leave this routine empty if you're an undergrad. If you want */
/* to use this routine, you'll need to change the value of the LINKCHANGE */
/* constant definition in prog3.c from 0 to 1 */

{
}
