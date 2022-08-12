package com.cuongd.study.algsp1.book.ch3;

import org.junit.Test;

import static com.cuongd.study.algsp1.book.ch3.RedBlackBST.BLACK;
import static com.cuongd.study.algsp1.book.ch3.RedBlackBST.RED;
import static org.junit.Assert.assertEquals;

public class Ex3325Test {
    @Test
    public void test() {
        Ex3325<String, Integer> st = new Ex3325<>();
        st.put("S", 0);
        assertEquals("S", st.root.key);

        st.put("E", 0);
        assertNode(st.root.left, "E", RED);

        st.put("A", 0);
        assertNode(st.root, "E", BLACK);
        assertNode(st.root.left, "A", RED);
        assertNode(st.root.right, "S", RED);

        st.put("R", 0);
        assertNode(st.root, "E", BLACK);
        assertNode(st.root.left, "A", BLACK);
        assertNode(st.root.right, "S", BLACK);
        assertNode(st.root.right.left, "R", RED);

        st.put("C", 0);
        st.put("H", 0);
        assertNode(st.root, "E", BLACK);
        assertNode(st.root.left, "C", BLACK);
        assertNode(st.root.left.left, "A", RED);
        assertNode(st.root.right, "R", BLACK);
        assertNode(st.root.right.left, "H", RED);
        assertNode(st.root.right.right, "S", RED);

        st.put("X", 0);
        st.put("M", 0);
        st.put("P", 0);
        st.put("L", 0);
        assertNode(st.root, "M", BLACK);
        assertNode(st.root.left, "E", RED);
        assertNode(st.root.left.left, "C", BLACK);
        assertNode(st.root.left.left.left, "A", RED);
        assertNode(st.root.left.right, "L", BLACK);
        assertNode(st.root.left.right.left, "H", RED);
        assertNode(st.root.right, "R", RED);
        assertNode(st.root.right.left, "P", BLACK);
        assertNode(st.root.right.right, "X", BLACK);
    }

    private void assertNode(RedBlackBST<String, Integer>.Node x, String key, boolean color) {
        assertEquals(key, x.key);
        assertEquals(color, x.color);
    }
}
