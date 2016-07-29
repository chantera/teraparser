package jp.naist.cl.srparser.util;

import java.io.Serializable;

/**
 * jp.naist.cl.srparser.util
 *
 * @author Hiroki Teranishi
 */
public class Triple<L, M, R> implements Serializable {
    private static final long serialVersionUID = 6381934395925396386L;

    public final L left;
    public final M middle;
    public final R right;

    public Triple(final L left, final M middle, final R right) {
        this.left = left;
        this.middle = middle;
        this.right = right;
    }

    public static <L, M, R> Triple<L, M, R> create(final L left, final M middle, final R right) {
        return new Triple<>(left, middle, right);
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof Triple<?, ?, ?>) {
            final Triple<?, ?, ?> other = (Triple<?, ?, ?>) obj;
            return left.equals(other.left)
                && middle.equals(other.middle)
                && right.equals(other.right);
        }
        return false;
    }

    public L getLeft() {
        return left;
    }

    public M getMiddle() {
        return middle;
    }

    public R getRight() {
        return right;
    }

    @Override
    public int hashCode() {
        return (left == null ? 0 : left.hashCode()) ^
                (middle == null ? 0 : middle.hashCode()) ^
                (right == null ? 0 : right.hashCode());
    }

    @Override
    public String toString() {
        return "(" + left + "," + middle + "," + right + ")";
    }
}
