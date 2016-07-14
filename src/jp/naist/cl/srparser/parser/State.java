package jp.naist.cl.srparser.parser;

import jp.naist.cl.srparser.model.Feature;
import jp.naist.cl.srparser.model.Token;

import java.util.List;
import java.util.Set;

/**
 * jp.naist.cl.srparser.parser
 *
 * @author Hiroki Teranishi
 */
public class State {
    public final int step;
    public final Token stackTop;
    public final Token bufferHead;
    private final boolean initial;
    private final boolean terminal;
    public final int[] features;
    public final Set<Action> nextActions;
    public final State prevState;
    public final Action prevAction;
    public final Action goldAction;

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

    public Boolean isTerminal() {
        return terminal;
    }

    /**
     * State.from(state, action).createNext(stack, buffer, arcSet);
     */
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
}
