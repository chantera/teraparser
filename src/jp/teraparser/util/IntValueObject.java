package jp.teraparser.util;

/**
 * jp.teraparser.util
 *
 * @author Hiroki Teranishi
 */
public class IntValueObject implements Comparable<IntValueObject> {
    protected final int value;

    protected IntValueObject(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof IntValueObject) {
            final IntValueObject other = (IntValueObject) obj;
            return this.value == other.value;
        }
        return false;
    }

    @Override
    public int compareTo(IntValueObject another) {
        return this.value - another.value;
    }

    @Override
    public int hashCode() {
        return value;
    }

    @Override
    public String toString() {
        return Integer.toString(value);
    }
}
