package jp.naist.cl.srparser.parser;

import jp.naist.cl.srparser.model.Feature;
import jp.naist.cl.srparser.model.Sentence;
import jp.naist.cl.srparser.model.Token;

import java.util.*;

/**
 * jp.naist.cl.srparser.parser
 *
 * @author Hiroki Teranishi
 */
public class State {
    // public final int step;
    // public final Token stackTop;
    // // public final Token bufferHead;
    // private final boolean initial;
    // private final boolean terminal;
    // public final Set<Action> nextActions;
    // public final State prevState;
    // public final Action prevAction;
    // public final Action goldAction;

    final int step;
    final Token[] tokens;
    final ArrayDeque<Integer> stack;
    final int bufferHead;
    final int bufferSize;
    final Arc[] arcs;
    final int[] features;
    final Set<Action> possibleActions;
    // transition info
    final State prevState;
    final Action prevAction;

    /**
     * Generate Initial State
     */
    public State(Sentence sentence) {
        this.step            = 0;
        this.tokens          = sentence.tokens;
        this.stack           = new ArrayDeque<>();
        this.stack.push(0);
        this.bufferHead      = 1;
        this.bufferSize      = tokens.length - bufferHead;
        this.arcs            = new Arc[tokens.length];
        this.features        = Feature.extract(this);
        this.possibleActions = Action.getPossibleActions(this);
        this.prevState       = null;
        this.prevAction      = null;
    }

    State(State prevState, Action prevAction, Arc prevArc, ArrayDeque<Integer> stack, int bufferHead) {
        this.step            = prevState.step + 1;
        this.tokens          = prevState.tokens;
        this.stack           = stack;
        this.bufferHead      = bufferHead;
        this.bufferSize      = tokens.length - bufferHead;
        if (prevArc != null) {
            this.arcs        = prevState.arcs.clone(); // shallow copy, which does not make copies of elements.
            this.arcs[prevArc.dependent] = prevArc;
        } else {
            this.arcs        = prevState.arcs;         // reference
        }
        this.features        = Feature.extract(this);
        this.possibleActions = Action.getPossibleActions(this);
        this.prevState       = prevState;
        this.prevAction      = prevAction;
    }

    public Token getStackTopToken() {
        return tokens[stack.getFirst()];
    }

    public Token getStackToken(int position) {
        if (position == 0) {
            return tokens[stack.getFirst()];
        }
        int i = 0;
        for (Integer index : stack) {
            if (i == position) {
                return tokens[index];
            }
            i++;
        }
        return tokens[-1]; // throw new IndexOutOfBoundsException();
    }

    public Token getStackTokenOrDefault(int position, Token defaultToken) {
        if (position == 0) {
            return tokens[stack.getFirst()];
        }
        int i = 0;
        for (Integer index : stack) {
            if (i == position) {
                return tokens[index];
            }
            i++;
        }
        return defaultToken;
    }

    public boolean hasArc(int dependent) {
        return arcs[dependent] != null;
    }

    public Token getBufferHeadToken() {
        return tokens[bufferHead];
    }

    public Token getBufferToken(int position) {
        return tokens[bufferHead + position];
    }

    public Token getBufferTokenOrDefault(int position, Token defaultToken) {
        try {
            return tokens[bufferHead + position];
        } catch (IndexOutOfBoundsException e) {
            return defaultToken;
        }
    }

    /*
    public Set<Arc> getArcSet() {
        Set<Arc> arcSet = new TreeSet<Arc>((Arc a1, Arc a2) -> a1.dependent - a2.dependent);
        State state = this;
        do {
            if (state.prevArc != null) {
                arcSet.add(state.prevArc);
            }
        } while ((state = state.prevState) != null);
        return arcSet;
    }
    */

    /*
    public State(final List<Token> stack, final List<Token> buffer, final Set<Arc> arcSet) {
        this(stack, buffer, arcSet, null, null);
    }

    public State(final List<Token> stack, final List<Token> buffer, final Set<Arc> arcSet, State prevState, Action prevAction) {
        this.prevState = prevState;
        this.prevAction = prevAction;
        stackTop = stack.get(stack.size() - 1);
        if (buffer.size() == 0) {
            terminal = true;
            bufferHead = null;
            initial = false;
            step = prevState.step + 1;
        } else {
            terminal = false;
            bufferHead = buffer.get(0);
            if (prevState == null) {
                initial = true;
                step = 0;
            } else {
                initial = false;
                step = prevState.step + 1;
            }
        }
        features = Feature.extract(stack, buffer);
        nextActions = Action.getActions(this, arcSet);
        goldAction = Action.getGoldAction(this, buffer, arcSet);
    }

    public Boolean isInitial() {
        return initial;
    }
    */

    public Boolean isTerminal() {
        return bufferHead == tokens.length;
    }

    /**
     * State.from(state, action).createNext(stack, buffer, arcSet);
     */
    /*
    public static Builder from(State state, Action action) {
        return new Builder(state, action);
    }

    protected static class Builder {
        private State prevState;
        private Action prevAction;

        private Builder(State prevState, Action prevAction) {
            this.prevState = prevState;
            this.prevAction = prevAction;
        }

        public State createNext(List<Token> stack, List<Token> buffer, Set<Arc> arcSet) {
            return new State(stack, buffer, arcSet, prevState, prevAction);
        }
    }
    */

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
