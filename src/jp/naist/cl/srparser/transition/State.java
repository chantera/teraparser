package jp.naist.cl.srparser.transition;

import jp.naist.cl.srparser.model.Feature;
import jp.naist.cl.srparser.model.Sentence;
import jp.naist.cl.srparser.model.Token;

import java.util.ArrayDeque;
import java.util.Set;


/**
 * jp.naist.cl.srparser.transition
 *
 * @author Hiroki Teranishi
 */
public class State {
    final int step;
    final Token[] tokens;
    final ArrayDeque<Integer> stack;
    final int bufferHead;
    public final Arc[] arcs;
    public final int[] features;
    public final Set<Action> possibleActions;
    public final State prevState;
    public final Action prevAction;

    /**
     * Generate Initial State
     */
    public State(Sentence sentence) {
        this.step            = 0;
        this.tokens          = sentence.tokens;
        this.stack           = new ArrayDeque<>();
        this.stack.push(0);
        this.bufferHead      = 1;
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

    public Boolean isInitial() {
        return step == 0;
    }

    public Boolean isTerminal() {
        return bufferHead == tokens.length;
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

    public Action[] getActions() {
        Action[] actions = new Action[step];
        State state = this;
        do {
            actions[state.step - 1] = state.prevAction;
        } while ((state = state.prevState) != null && !state.isInitial());
        return actions;
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
