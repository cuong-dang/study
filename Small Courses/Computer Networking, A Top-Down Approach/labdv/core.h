/** Core node functions. */
#ifndef CORE_H
#define CORE_H

struct distance_table
{
  int neighbors[4];
  int costs[4][4];
};

struct rtpkt
{
  int sourceid;
  int destid;
  int mincost[4];
};

/** Initialize node's distance table and broadcast to neighbor nodes. */
void rinit(int nodeid, struct distance_table *dt,
           int c0, int c1, int c2, int c3, int *isconnected,
           void printdt(struct distance_table *dt));

/** Broadcast node's distance table to neighbor nodes. */
void broadcast(int nodeid, struct distance_table *dt, int *isconnected);

/** Update node's state upon receiving rtpkt. */
void rtupdate(int nodeid, struct distance_table *dt, struct rtpkt *rcvdpkt,
              int *isconnected,
              void printdt(struct distance_table *dt));

/** Handle a change in link cost. */
void linkhandler(int nodeid, struct distance_table *dt, int *isconnected,
                 int linkid, int newcost);

#endif
