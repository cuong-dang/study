#include <stdio.h>

extern struct rtpkt {
  int sourceid;       /* id of sending router sending this pkt */
  int destid;         /* id of router to which pkt being sent
                         (must be an immediate neighbor) */
  int mincost[4];    /* min cost to node 0 ... 3 */
  };

extern int TRACE;
extern int YES;
extern int NO;

struct distance_table
{
  int costs[4][4];
} dt0;


/* students to write the following two routines, and maybe some others */
extern float clocktime;
void printdt0(struct distance_table *dt0);
void tolayer2(struct rtpkt);
void broadcast();

void rtinit0()
{
  int i, j;
  struct rtpkt rtpkt;

  /* costs: 0: itself, -1: unknown, 999: unreachable */
  rtpkt.mincost[0] = dt0.costs[0][0] = 0;
  rtpkt.mincost[1] = dt0.costs[0][1] = 1;
  rtpkt.mincost[2] = dt0.costs[0][2] = 3;
  rtpkt.mincost[3] = dt0.costs[0][3] = 7;
  for (i = 1; i < 4; i++)
    for (j = 0; j < 4; j++)
      if (i == j) dt0.costs[i][j] = 0;
      else dt0.costs[i][j] = -1;
  printf("time %.0f: node0 initialization\n", clocktime);
  printdt0(&dt0);
  broadcast();
}

void rtupdate0(rcvdpkt)
  struct rtpkt *rcvdpkt;
{
  int dtupdated = 0;
  int new_cost_via;
  int i;

  printf("time %.0f: from %d to %d\n",
         clocktime, rcvdpkt->sourceid, rcvdpkt->destid);
  for (i = 0; i < 4; i++) {
    if (dt0.costs[rcvdpkt->sourceid][i] == rcvdpkt->mincost[i]) {
      continue;
    } else {
      printf("  new mindv update: %d->%d: %d->%d\n", rcvdpkt->sourceid, i,
             dt0.costs[rcvdpkt->sourceid][i], rcvdpkt->mincost[i]);
      dt0.costs[rcvdpkt->sourceid][i] = rcvdpkt->mincost[i];
      new_cost_via = dt0.costs[0][rcvdpkt->sourceid] + rcvdpkt->mincost[i];
      if (dt0.costs[0][i] > new_cost_via) {
        printf("  new mindv calc: 0->%d: %d->%d\n",
               i, dt0.costs[0][i], new_cost_via);
        if (!dtupdated) dtupdated = 1;
        dt0.costs[0][i] = new_cost_via;
      }
    }
  }
  if (dtupdated) broadcast();
}

void broadcast()
{
  struct rtpkt rtpkt;
  int i;

  rtpkt.sourceid = 0;
  rtpkt.mincost[0] = dt0.costs[0][0];
  rtpkt.mincost[1] = dt0.costs[0][1];
  rtpkt.mincost[2] = dt0.costs[0][2];
  rtpkt.mincost[3] = dt0.costs[0][3];
  for (i = 1; i < 4; i++) {
    rtpkt.destid = i;
    tolayer2(rtpkt);
  }
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
