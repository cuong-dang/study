package com.cuongd.study.algs.book.ch5;

import static com.cuongd.study.algs.book.ch5.MSD.charAt;

public class Ex5116 {
    public static void sort(Node head) {
        Node tail = head;
        while (tail.next != null) {
            tail = tail.next;
        }
        sort(head, tail, 0);
    }

    private static void sort(Node lo, Node hi, int d) {
        if (lo == null || hi == lo.prev || hi == lo) return;
        Node lt = lo, gt = hi;
        int v = charAt(lo.key, d);
        Node i = lo.next;
        while (gt.next != i) {
            int t = charAt(i.key, d);
            if (t < v) {
                exch(lt, i);
                lt = lt.next;
                i = i.next;
            } else if (t > v) {
                exch(i, gt);
                gt = gt.prev;
            } else {
                i = i.next;
            }
        }
        sort(lo, lt.prev, d);
        if (v >= 0) sort(lt, gt, d+1);
        sort(gt.next, hi, d);
    }

    private static void exch(Node x, Node y) {
        String xKey = x.key;
        x.key = y.key;
        y.key = xKey;
    }

    public static class Node {
        String key;
        Node next;
        Node prev;

        public Node(String key) {
            this.key = key;
            this.next = null;
            this.prev = null;
        }
    }

    public static void main(String[] args) {
        Node no = new Node("no");
        Node is = new Node("is");
        Node th = new Node("th");
        Node ti = new Node("ti");
        Node fo = new Node("fo");
        Node al = new Node("al");
        Node go = new Node("go");
        Node pe = new Node("pe");
        Node to = new Node("to");
        Node co = new Node("co");
        Node to2 = new Node("to");
        Node th2 = new Node("th");
        Node ai = new Node("ai");
        Node of = new Node("of");
        Node th3 = new Node("th");
        Node pa = new Node("pa");

        no.prev = null; no.next = is;
        is.prev = no; is.next = th;
        th.prev = is; th.next = ti;
        ti.prev = th; ti.next = fo;
        fo.prev = ti; fo.next = al;
        al.prev = fo; al.next = go;
        go.prev = al; go.next = pe;
        pe.prev = go; pe.next = to;
        to.prev = pe; to.next = co;
        co.prev = to; co.next = to2;
        to2.prev = co; to2.next = th2;
        th2.prev = to2; th2.next = ai;
        ai.prev = th2; ai.next = of;
        of.prev = ai; of.next = th3;
        th3.prev = of; th3.next = pa;
        pa.prev = th3; pa.next = null;

        sort(no);
        for (Node x = no; x != null; x = x.next) {
            System.out.printf("%s ", x.key);
        }
        System.out.println();
    }
}
