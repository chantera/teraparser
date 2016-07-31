package jp.teraparser.transition;

/**
 * jp.teraparser.transition
 *
 * @author Hiroki Teranishi
 */
public class Arc {
    public final int head;
    public final int dependent;

    public Arc(int head, int dependent) {
        this.head = head;
        this.dependent = dependent;
    }

    public boolean isLeft() {
        return dependent < head;
    }

    public boolean isRight() {
        return head < dependent;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof Arc) {
            final Arc other = (Arc) obj;
            return this.head == other.head && this.dependent == other.dependent;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return 31 * (31 * 17 + head) + dependent;
    }

    @Override
    public String toString() {
        if (isLeft()) {
            return "(d:" + dependent + " <- h:" + head + ")";
        } else {
            return "(h:" + head + " -> d:" + dependent + ")";
        }
    }
}
