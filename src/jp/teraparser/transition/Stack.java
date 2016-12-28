package jp.teraparser.transition;

import java.util.Arrays;

/**
 * Resizable-array implementation of Deque which works like java.util.ArrayDeque
 *
 * jp.teraparser.util
 *
 * @author Hiroki Teranishi
 */
public class Stack implements Cloneable {
    private static final int MIN_INITIAL_CAPACITY = 8;

    private int[] elements;
    private int head;
    private int tail;

    /**
     * Allocates empty array to hold the given number of elements.
     */
    private void allocateElements(int numElements) {
        int initialCapacity = MIN_INITIAL_CAPACITY;
        // Find the best power of two to hold elements.
        // Tests "<=" because arrays aren't kept full.
        if (numElements >= initialCapacity) {
            initialCapacity = numElements;
            initialCapacity |= (initialCapacity >>>  1);
            initialCapacity |= (initialCapacity >>>  2);
            initialCapacity |= (initialCapacity >>>  4);
            initialCapacity |= (initialCapacity >>>  8);
            initialCapacity |= (initialCapacity >>> 16);
            initialCapacity++;

            if (initialCapacity < 0) { // Too many elements, must back off
                initialCapacity >>>= 1;// Good luck allocating 2 ^ 30 elements
            }
        }
        elements = new int[initialCapacity];
    }

    /**
     * Doubles the capacity of this deque.  Call only when full, i.e.,
     * when head and tail have wrapped around to become equal.
     */
    private void doubleCapacity() {
        assert head == tail;
        int p = head;
        int n = elements.length;
        int r = n - p; // number of elements to the right of p
        int newCapacity = n << 1;
        if (newCapacity < 0) {
            throw new IllegalStateException("Sorry, deque too big");
        }
        int[] a = new int[newCapacity];
        System.arraycopy(elements, p, a, 0, r);
        System.arraycopy(elements, 0, a, r, p);
        elements = a;
        head = 0;
        tail = n;
    }

    public Stack() {
        elements = new int[16];
    }

    public Stack(int numElements) {
        allocateElements(numElements);
    }

    public void push(int e) {
        elements[head = (head - 1) & (elements.length - 1)] = e;
        if (head == tail) {
            doubleCapacity();
        }
    }

    public int pop() {
        int h = head;
        int result = elements[h];
        elements[h] = 0;
        head = (h + 1) & (elements.length - 1);
        return result;
    }

    public int top() {
        return elements[head];
    }

    public int getFirst() {
        return top();
    }

    public int get(int position, int defaultValue) {
        if (position < 0 || position >= size()) {
            return defaultValue;
        }
        int mask = elements.length - 1;
        int h = head;
        for (int i = 0; i < position; i++) {
            h = (h + 1) & mask;
        }
        return elements[h];
    }

    public int size() {
        return (tail - head) & (elements.length - 1);
    }

    public boolean isEmpty() {
        return head == tail;
    }

    public Stack clone() {
        try {
            Stack result = (Stack) super.clone();
            result.elements = Arrays.copyOf(elements, elements.length);
            return result;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
