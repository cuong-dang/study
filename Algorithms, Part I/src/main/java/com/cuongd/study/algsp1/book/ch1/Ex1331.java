package com.cuongd.study.algsp1.book.ch1;

public class Ex1331<Item> {
    DoubleNode<Item> first;
    DoubleNode<Item> last;

    public boolean isEmpty() {
        return first == null;
    }

    public Item peek() {
        return first.value;
    }

    public DoubleNode<Item> insertFirst(Item value) {
        DoubleNode<Item> node = new DoubleNode<>();
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

    public DoubleNode<Item> insertLast(Item value) {
        if (last == null) {
            return insertFirst(value);
        }
        DoubleNode<Item> node = new DoubleNode<>();
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
        DoubleNode<Item> cur;
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
        DoubleNode<Item> cur;
        for (cur = last; cur.prev != null; cur = cur.prev) {
            result.append(cur.value);
            result.append(",");
        }
        result.append(cur.value);
        result.append(">");
        return result.toString();
    }

    static public <Item> void insertBefore(DoubleNode<Item> node, Item value) {
        assert(node.prev != null);
        DoubleNode<Item> newNode = new DoubleNode<>(), prevNode = node.prev;
        newNode.value = value;
        newNode.next = node;
        node.prev = newNode;
        prevNode.next = newNode;
        newNode.prev = prevNode;
    }

    static public <Item> void insertAfter(DoubleNode<Item> node, Item value) {
        DoubleNode<Item> newNode = new DoubleNode<>();
        newNode.value = value;
        newNode.prev = node;
        newNode.next = node.next;
        node.next.prev = newNode;
        node.next = newNode;
    }

    static public <Item> void remove(DoubleNode<Item> node) {
        assert(node.prev != null);
        assert(node.next != null);
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }

    static class DoubleNode<Item> {
        Item value;
        DoubleNode<Item> next;
        DoubleNode<Item> prev;
    }
}
