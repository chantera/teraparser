package jp.teraparser.util;

import java.io.Serializable;

/**
 * jp.teraparser.util
 *
 * @author Hiroki Teranishi
 */
public class Tuple<L, R> implements Serializable {
    private static final long serialVersionUID = 3436252075895211268L;

    public final L left;
    public final R right;

    public Tuple(final L left, final R right) {
        this.left = left;
        this.right = right;
    }

    public static <L, R> Tuple<L, R> create(final L left, final R right) {
        return new Tuple<>(left, right);
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof Tuple<?, ?>) {
            final Tuple<?, ?> other = (Tuple<?, ?>) obj;
            return left.equals(other.left) && right.equals(other.right);
        }
        return false;
    }

    public L getLeft() {
        return left;
    }

    public R getRight() {
        return right;
    }

    private volatile int hashCode;

    @Override
    public int hashCode() {
        int h = hashCode;
        if (h == 0) {
            h = (left == null ? 0 : left.hashCode()) ^ (right == null ? 0 : right.hashCode());
            hashCode = h;
        }
        return h;
    }

    @Override
    public String toString() {
        return "(" + left + "," + right + ")";
    }
}
