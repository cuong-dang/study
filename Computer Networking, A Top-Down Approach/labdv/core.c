#include <stdio.h>
#include "core.h"

#define NUM_NODES 4
#define NOT_CONNECTED 999

extern float clocktime;
void tolayer2(struct rtpkt rtpkt);

void rinit(nodeid, dt, c0, c1, c2, c3, printdt)
  int nodeid, c0, c1, c2, c3;
  struct distance_table *dt;
  void printdt(struct distance_table *dt);

{
  int i, j;

  dt->costs[nodeid][0] = c0;
  dt->costs[nodeid][1] = c1;
  dt->costs[nodeid][2] = c2;
  dt->costs[nodeid][3] = c3;

  for (i = 0; i < NUM_NODES; i++)
    for (j = 0; j < NUM_NODES; j++)
      if (i != nodeid) {
        if (i == j) dt->costs[i][j] = 0;
        else dt->costs[i][j] = -1;
      }
  printf("time %.0f: node%d init\n", clocktime, nodeid);
  printdt(dt);
  broadcast(nodeid, dt);
}

void broadcast(nodeid, dt)
  int nodeid;
  struct distance_table *dt;

{
  struct rtpkt rtpkt;
  int i;

  rtpkt.sourceid = nodeid;
  for (i = 0; i < NUM_NODES; i++)
    rtpkt.mincost[i] = dt->costs[nodeid][i];
  for (i = 0; i < NUM_NODES; i++)
    if (i != nodeid && dt->costs[nodeid][i] != NOT_CONNECTED) {
      rtpkt.destid = i;
      tolayer2(rtpkt);
    }
}

void rtupdate(nodeid, dt, rcvdpkt, printdt)
  int nodeid;
  struct distance_table *dt;
  struct rtpkt *rcvdpkt;
  void printdt(struct distance_table *dt);

{
  int dtupdated = 0;
  int new_cost_via;
  int i;

  printf("time %.0f: from %d to %d\n",
         clocktime, rcvdpkt->sourceid, rcvdpkt->destid);
  for (i = 0; i < NUM_NODES; i++) {
    if (dt->costs[rcvdpkt->sourceid][i] != rcvdpkt->mincost[i]) {
      printf("  new mindv update: %d->%d: %d->%d\n", rcvdpkt->sourceid, i,
             dt->costs[rcvdpkt->sourceid][i], rcvdpkt->mincost[i]);
      dt->costs[rcvdpkt->sourceid][i] = rcvdpkt->mincost[i];
      new_cost_via = dt->costs[nodeid][rcvdpkt->sourceid] +
                       rcvdpkt->mincost[i];
      if (new_cost_via < dt->costs[nodeid][i]) {
        printf("  new mindv calc: %d->%d: %d->%d\n",
               nodeid, i, dt->costs[nodeid][i], new_cost_via);
        if (!dtupdated) dtupdated = 1;
        dt->costs[nodeid][i] = new_cost_via;
        printdt(dt);
      }
    }
  }
  if (dtupdated) broadcast(nodeid, dt);
}
