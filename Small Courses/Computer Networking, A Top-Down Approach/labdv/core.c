#include <stdio.h>
#include "core.h"

#define NUM_NODES 4
#define NOT_CONNECTED 999

extern float clocktime;
void tolayer2(struct rtpkt rtpkt);
static int find_mincost_to(struct distance_table *dt, int dest);

void rinit(nodeid, dt, c0, c1, c2, c3, isconnected, printdt)
  int nodeid, c0, c1, c2, c3;
  struct distance_table *dt;
  int *isconnected;
  void printdt(struct distance_table *dt);

{
  int i, j;

  dt->neighbors[0] = dt->costs[nodeid][0] = c0;
  dt->neighbors[1] = dt->costs[nodeid][1] = c1;
  dt->neighbors[2] = dt->costs[nodeid][2] = c2;
  dt->neighbors[3] = dt->costs[nodeid][3] = c3;

  for (i = 0; i < NUM_NODES; i++)
    for (j = 0; j < NUM_NODES; j++)
      if (i != nodeid) {
        if (i == j) dt->costs[i][j] = 0;
        else dt->costs[i][j] = -1;
      }
  printf("time %.0f: node%d init\n", clocktime, nodeid);
  printdt(dt);
  broadcast(nodeid, dt, isconnected);
}

void broadcast(nodeid, dt, isconnected)
  int nodeid;
  struct distance_table *dt;
  int *isconnected;

{
  struct rtpkt rtpkt;
  int i;

  rtpkt.sourceid = nodeid;
  for (i = 0; i < NUM_NODES; i++)
    rtpkt.mincost[i] = dt->costs[nodeid][i];
  for (i = 0; i < NUM_NODES; i++)
    if (isconnected[i]) {
      printf("broadcast %d->%d\n", nodeid, i);
      rtpkt.destid = i;
      tolayer2(rtpkt);
    }
}

void rtupdate(nodeid, dt, rcvdpkt, isconnected, printdt)
  int nodeid;
  struct distance_table *dt;
  struct rtpkt *rcvdpkt;
  int *isconnected;
  void printdt(struct distance_table *dt);

{
  int dtupdated = 0;
  int new_mincost;
  int i;

  printf("time %.0f: from %d to %d\n",
         clocktime, rcvdpkt->sourceid, rcvdpkt->destid);
  for (i = 0; i < NUM_NODES; i++) {
    if (dt->costs[rcvdpkt->sourceid][i] != rcvdpkt->mincost[i]) {
      // printf("  new mindv update: %d->%d: %d->%d\n", rcvdpkt->sourceid, i,
      //       dt->costs[rcvdpkt->sourceid][i], rcvdpkt->mincost[i]);
      dt->costs[rcvdpkt->sourceid][i] = rcvdpkt->mincost[i];
      new_mincost = find_mincost_to(dt, i);
      if (new_mincost != dt->costs[nodeid][i]) {
        printf("  new mindv calc: %d->%d: %d->%d\n",
               nodeid, i, dt->costs[nodeid][i], new_mincost);
        if (!dtupdated) dtupdated = 1;
        dt->costs[nodeid][i] = new_mincost;
      }
    }
  }
  if (dtupdated) broadcast(nodeid, dt, isconnected);
}

void linkhandler(nodeid, dt, isconnected, linkid, newcost)
  int nodeid, linkid, newcost;
  struct distance_table *dt;
  int *isconnected;
{
  int dtupdated = 0, i;

  printf(" new linkcost from %d->%d: %d\n", nodeid, linkid, newcost);
  dt->neighbors[linkid] = newcost;
  for (i = 0; i < NUM_NODES; i++) {
    if (i == nodeid) continue;
    int mincost = find_mincost_to(dt, i);
    if (mincost != dt->costs[nodeid][i]) {
      if (!dtupdated) dtupdated = 1;
      dt->costs[nodeid][i] = mincost;
    }
  }
  if (dtupdated) broadcast(nodeid, dt, isconnected);
}

static int find_mincost_to(dt, dest)
  struct distance_table *dt;
  int dest;

{
  int i, mincost = NOT_CONNECTED;

  for (i = 0; i < NUM_NODES; i++) {
     int newcost_to = (dt->neighbors[i] == 0) ? dt->neighbors[dest] :
                      (dt->neighbors[i] + dt->costs[i][dest]);
     if (newcost_to < mincost) mincost = newcost_to;
  }
  return mincost;
}
