package com.cuongd.study.algsp1.book.ch1;

/** An implementation for stack-ended queue or steque.*/

/* It's not completely clear from the exercise prompt what a steque really is.
 * For my own implementation, I see it as a mix of stacks and queues. The
 * following examples will be helpful.
 *
 * p1 p2 e3 e4 e5 --> 3 4 5 2 1
 * p1 e2 p3 e4 p5 e6 --> 6 5 4 3 2 1
 * e1 e2 p3 p4 p5 --> 5 4 3 1 2
 */

/* The following code, even though correct, is not elegant since I was trying
 * to reuse Ex1331's data structure.
 */
public class Ex1332<Item> {

    private final Ex1331<ValueWithTag<Item>> head;
    private final MyStack<Ex1331.DoubleNode<ValueWithTag<Item>>> queueHeads;
    private int N;

    public Ex1332() {
        head = new Ex1331<>();
        queueHeads = new MyStack<>();
        N = 0;
    }

    public boolean isEmpty() {
        return N == 0;
    }

    public int size() {
        return N;
    }

    public void push(Item value) {
        N++;
        ValueWithTag<Item> ValueWithTag = new ValueWithTag<>(Tag.STACK, value);
        head.insertFirst(ValueWithTag);
    }

    public void enqueue(Item value) {
        N++;
        ValueWithTag<Item> valueWithTag = new ValueWithTag<>(Tag.QUEUE, value);
        if (head.isEmpty() || head.peek().tag == Tag.STACK) {
            queueHeads.push(head.insertFirst(valueWithTag));
        } else {
            head.insertFirst(valueWithTag);
        }
    }

    public Item pop() {
        N--;
        ValueWithTag<Item> p = head.peek();
        if (p.tag == Tag.STACK) {
            head.removeFirst();
            return p.val;
        }
        Ex1331.DoubleNode<ValueWithTag<Item>> queueNode = queueHeads.pop();
        if (!isLastInQueue()) {
            queueHeads.push(queueNode.prev);
        }
        head.remove(queueNode);
        return queueNode.value.val;
    }

    public boolean isLastInQueue() {
        ValueWithTag<Item> t = head.peek();
        head.removeFirst();
        boolean result = head.isEmpty() ||
                (t.tag == Tag.QUEUE && head.peek().tag == Tag.STACK);
        head.insertFirst(t);
        return result;
    }

    static class ValueWithTag<Item> {
        public Item val;
        public Tag tag;

        public ValueWithTag(Tag tag, Item val) {
            this.val = val;
            this.tag = tag;
        }
    }

    enum Tag {
        STACK,
        QUEUE
    }
}
