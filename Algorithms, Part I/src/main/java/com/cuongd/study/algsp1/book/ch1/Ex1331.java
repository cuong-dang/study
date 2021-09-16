package com.cuongd.study.algsp1.book.ch1;

public class Ex1331 {
    DoubleNode first;
    DoubleNode last;

    public DoubleNode insertFirst(int value) {
        DoubleNode node = new DoubleNode();
        node.value = value;
        if (first != null) {
            node.next = first;
            first.prev = node;
        }
        first = node;
        if (last == null) {
            last = first;
        }
        return node;
    }

    public DoubleNode insertLast(int value) {
        if (last == null) {
            return insertFirst(value);
        }
        DoubleNode node = new DoubleNode();
        node.value = value;
        last.next = node;
        node.prev = last;
        last = node;
        return node;
    }

    public void removeFirst() {
        assert(first != null);
        if (first == last) {
            first = last = null;
        } else {
            first = first.next;
            first.prev = null;
        }
    }

    public void removeLast() {
        assert(last != null);
        if (first == last) {
            removeFirst();
        } else {
            last.prev.next = null;
            last = last.prev;
        }
    }

    public String forwardString() {
        StringBuilder result = new StringBuilder();
        result.append("<");
        DoubleNode cur;
        for (cur = first; cur.next != null; cur = cur.next) {
            result.append(cur.value);
            result.append(",");
        }
        result.append(cur.value);
        result.append(">");
        return result.toString();
    }

    public String backwardString() {
        StringBuilder result = new StringBuilder();
        result.append("<");
        DoubleNode cur;
        for (cur = last; cur.prev != null; cur = cur.prev) {
            result.append(cur.value);
            result.append(",");
        }
        result.append(cur.value);
        result.append(">");
        return result.toString();
    }

    static public void insertBefore(DoubleNode node, int value) {
        assert(node.prev != null);
        DoubleNode newNode = new DoubleNode(), prevNode = node.prev;
        newNode.value = value;
        newNode.next = node;
        node.prev = newNode;
        prevNode.next = newNode;
        newNode.prev = prevNode;
    }

    static public void insertAfter(DoubleNode node, int value) {
        DoubleNode newNode = new DoubleNode();
        newNode.value = value;
        newNode.prev = node;
        newNode.next = node.next;
        node.next.prev = newNode;
        node.next = newNode;
    }

    static public void remove(DoubleNode node) {
        assert(node.prev != null);
        assert(node.next != null);
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }

    static class DoubleNode {
        int value;
        DoubleNode next;
        DoubleNode prev;
    }
}
