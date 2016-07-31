package jp.teraparser.transition;

import jp.teraparser.model.Feature;
import jp.teraparser.model.Sentence;
import jp.teraparser.model.Token;
import jp.teraparser.util.HashUtils;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;


/**
 * jp.teraparser.transition
 *
 * @author Hiroki Teranishi
 */
public class State {
    public final int step;
    final Token[] tokens;
    private final int tokenLength;
    final Stack stack;
    final int bufferHead;
    public final Arc[] arcs;
    final int[] leftmost;
    final int[] rightmost;
    private int[] features;
    private Set<Action> possibleActions;
    public final State prevState;
    public final Action prevAction;
    private double score; // only this property is mutable and optionally used.

    /**
     * Generate Initial State
     */
    public State(Sentence sentence) {
        this.step            = 0;
        this.tokens          = sentence.tokens;
        this.tokenLength     = tokens.length;
        this.stack           = new Stack();
        this.stack.push(0);
        this.bufferHead      = 1;
        this.arcs            = new Arc[tokenLength]; // index: dependent, value: head
        this.leftmost        = new int[tokenLength]; // index: head, value: leftmost dependent
        this.rightmost       = new int[tokenLength]; // index: head, value: rightmost dependent
        Arrays.fill(this.leftmost, Integer.MAX_VALUE);
        Arrays.fill(this.rightmost, -1);
        this.prevState       = null;
        this.prevAction      = null;
    }

    State(State prevState, Action prevAction, Arc prevArc, Stack stack, int bufferHead) {
        this.step            = prevState.step + 1;
        this.tokens          = prevState.tokens;
        this.tokenLength     = tokens.length;
        this.stack           = stack;
        this.bufferHead      = bufferHead;
        if (prevArc != null) {
            this.arcs        = prevState.arcs.clone(); // shallow copy, which does not make copies of elements.
            this.arcs[prevArc.dependent] = prevArc;
            // update leftmost and rightmost.
            if (prevArc.isLeft() && prevArc.dependent < prevState.leftmost[prevArc.head]) {
                this.leftmost  = prevState.leftmost.clone();
                this.leftmost[prevArc.head] = prevArc.dependent;
                this.rightmost = prevState.rightmost;
            } else if (prevArc.isRight() && prevArc.dependent > prevState.rightmost[prevArc.head]) {
                this.rightmost = prevState.rightmost.clone();
                this.rightmost[prevArc.head] = prevArc.dependent;
                this.leftmost  = prevState.leftmost;
            } else {
                this.leftmost  = prevState.leftmost;
                this.rightmost = prevState.rightmost;
            }
        } else {
            this.arcs        = prevState.arcs;         // reference
            this.leftmost    = prevState.leftmost;
            this.rightmost   = prevState.rightmost;
        }
        this.prevState       = prevState;
        this.prevAction      = prevAction;
    }

    public Boolean isInitial() {
        return step == 0;
    }

    public Boolean isTerminal() {
        return bufferHead == tokenLength;
    }

    public Token getToken(int index) {
        return tokens[index];
    }

    public Token getTokenOrDefault(int index, Token defaultToken) {
        return (index < tokenLength && index > -1) ? tokens[index] : defaultToken;
    }

    public Token getStackTopToken() {
        return tokens[stack.top()];
    }

    public Token getStackToken(int position) {
        if (position == 0) {
            return tokens[stack.top()];
        }
        return tokens[stack.get(position, -1)]; // throw new IndexOutOfBoundsException();
    }

    public Token getStackTokenOrDefault(int position, Token defaultToken) {
        if (position == 0) {
            return tokens[stack.top()];
        }
        int index = stack.get(position, -1);
        if (index != -1) {
            return tokens[index];
        }
        return defaultToken;
    }

    public Token getBufferHeadToken() {
        return tokens[bufferHead];
    }

    public Token getBufferToken(int position) {
        return tokens[bufferHead + position];
    }

    public Token getBufferTokenOrDefault(int position, Token defaultToken) {
        return getTokenOrDefault(bufferHead + position, defaultToken);
    }

    public Token getLeftmostToken(int index) {
        return tokens[leftmost[index]];
    }

    public Token getLeftmostTokenOrDefault(int index, Token defaultToken) {
        return index < leftmost.length ? getTokenOrDefault(leftmost[index], defaultToken) : defaultToken;
    }

    public Token getRightmostToken(int index) {
        return tokens[rightmost[index]];
    }

    public Token getRightmostTokenOrDefault(int index, Token defaultToken) {
        return index < rightmost.length ? getTokenOrDefault(rightmost[index], defaultToken) : defaultToken;
    }

    public boolean hasArc(int head, int dependent) {
        return (arcs[dependent] != null) && (arcs[dependent].head == head);
    }

    public boolean hasHeadArc(int head) {
        for (Arc arc : arcs) {
            if (arc.head == head) {
                return true;
            }
        }
        return false;
    }

    public boolean hasDependentArc(int dependent) {
        return arcs[dependent] != null;
    }

    public Set<Action> getPossibleActions() {
        // lazily initialization
        if (possibleActions == null) {
            possibleActions = Action.getPossibleActions(this);
        }
        return possibleActions;
    }

    public int[] getFeatures() {
        // lazily initialization
        if (features == null) {
            features = Feature.extract(this);
        }
        return features;
    }

    public void setScore(double value) {
        score = value;
    }

    public double getScore() {
        return score;
    }

    /**
     * get iterator from the initial state of this.
     */
    public StateIterator getIterator() {
        return new StateIterator(this);
    }

    public class StateIterator implements Iterator<State> {
        private State[] states;
        private int index;

        private StateIterator(State state) {
            states = new State[state.step + 1];
            do {
                states[state.step] = state;
                state = state.prevState;
            } while (state != null);
            index = 0;
        }

        @Override
        public boolean hasNext() {
            return index < states.length;
        }

        @Override
        public State next() {
            return states[index++];
        }
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof State) {
            final State other = (State) obj;
            if (step != other.step || bufferHead != other.bufferHead) {
                return false;
            }
            if (prevAction != null && prevAction != other.prevAction) {
                return false;
            }
            if (prevState != null && !prevState.equals(other.prevState)) {
                return false;
            }
            return true;
        }
        return false;
    }

    // Lazily initialized, cached hashCode
    private volatile int hashCode;

    @Override
    public int hashCode() {
        int result = hashCode;
        if (result == 0) {
            result = new HashUtils.HashCodeBuilder()
                    .append(step)
                    .append(getFeatures())
                    .append(prevAction.index)
                    .toHashCode();
            hashCode = result;
        }
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("step=").append(step);
        sb.append(", s1: ").append(getStackTokenOrDefault(1, null));
        sb.append(", s0: ").append(getStackTokenOrDefault(0, null));
        sb.append(", b0: ").append(getBufferTokenOrDefault(0, null));
        sb.append(", b1: ").append(getBufferTokenOrDefault(1, null));
        return sb.toString();
    }
}
