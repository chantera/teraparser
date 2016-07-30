package jp.naist.cl.srparser.util;

import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Resizable-array implementation of Deque which works like java.util.ArrayDeque
 *
 * jp.naist.cl.srparser.util
 *
 * @author Hiroki Teranishi
 */
public class Deque implements Iterable<Integer>, Cloneable {
    private static final int MIN_INITIAL_CAPACITY = 8;

    private Integer[] elements;
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
        elements = new Integer[initialCapacity];
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
        Integer[] a = new Integer[newCapacity];
        System.arraycopy(elements, p, a, 0, r);
        System.arraycopy(elements, 0, a, r, p);
        elements = a;
        head = 0;
        tail = n;
    }

    public Deque() {
        this(16);
    }

    public Deque(int numElements) {
        allocateElements(numElements);
    }

    public void addFirst(Integer e) {
        if (e == null) {
            throw new IllegalArgumentException();
        }
        elements[head = (head - 1) & (elements.length - 1)] = e;
        if (head == tail) {
            doubleCapacity();
        }
    }

    public void addLast(Integer e) {
        if (e == null) {
            throw new IllegalArgumentException();
        }
        elements[tail] = e;
        if ( (tail = (tail + 1) & (elements.length - 1)) == head) {
            doubleCapacity();
        }
    }

    public boolean push(int e) {
        addFirst(e);
        return true;
    }

    public boolean add(int e) {
        addLast(e);
        return true;
    }

    public int removeFirst() {
        int h = head;
        Integer result = elements[h];
        if (result == null) {
            throw new NoSuchElementException();
        }
        elements[h] = null;
        head = (h + 1) & (elements.length - 1);
        return result;
    }

    public int removeLast() {
        int t = (tail - 1) & (elements.length - 1);
        Integer result = elements[t];
        if (result == null) {
            throw new NoSuchElementException();
        }
        elements[t] = null;
        tail = t;
        return result;
    }

    public int pop() {
        return removeFirst();
    }

    public int getFirst() {
        Integer result = elements[head];
        if (result == null) {
            throw new NoSuchElementException();
        }
        return result;
    }

    public int getLast() {
        Integer result = elements[(tail - 1) & (elements.length - 1)];
        if (result == null) {
            throw new NoSuchElementException();
        }
        return result;
    }

    public int size() {
        return (tail - head) & (elements.length - 1);
    }

    public boolean isEmpty() {
        return head == tail;
    }

    public Iterator<Integer> iterator() {
        return new DequeIterator();
    }

    private class DequeIterator implements Iterator<Integer> {
        /**
         * Index of element to be returned by subsequent call to next.
         */
        private int cursor = head;

        /**
         * Tail recorded at construction (also in remove), to stop
         * iterator and also to check for comodification.
         */
        private int fence = tail;

        /**
         * Index of element returned by most recent call to next.
         * Reset to -1 if element is deleted by a call to remove.
         */
        private int lastRet = -1;

        public boolean hasNext() {
            return cursor != fence;
        }

        public Integer next() {
            if (cursor == fence) {
                throw new NoSuchElementException();
            }
            Integer result = elements[cursor];
            // This check doesn't catch all possible comodifications,
            // but does catch the ones that corrupt traversal
            if (tail != fence || result == null) {
                throw new ConcurrentModificationException();
            }
            lastRet = cursor;
            cursor = (cursor + 1) & (elements.length - 1);
            return result;
        }
    }

    public Deque clone() {
        try {
            Deque result = (Deque) super.clone();
            result.elements = Arrays.copyOf(elements, elements.length);
            return result;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
