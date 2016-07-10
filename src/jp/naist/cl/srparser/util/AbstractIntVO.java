package jp.naist.cl.srparser.util;

/**
 * jp.naist.cl.srparser.util
 *
 * @author Hiroki Teranishi
 */
public abstract class AbstractIntVO {
    protected final int value;

    protected AbstractIntVO(int value) {
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
        if (obj instanceof AbstractIntVO) {
            final AbstractIntVO other = (AbstractIntVO) obj;
            return this.value == other.value;
        }
        return false;
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
