package jp.naist.cl.srparser.parser;

import jp.naist.cl.srparser.model.Token;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * jp.naist.cl.srparser.parser
 *
 * @author Hiroki Teranishi
 */
public enum Action {
    LEFT(0) {
        @Override
        protected State apply(LinkedList<Token> stack, LinkedList<Token> buffer, Set<Arc> arcSet) {
            Token head = buffer.getFirst();
            Token dependent = stack.removeLast();
            arcSet.add(new Arc(head.id, dependent.id));
            return null;
        }
    },
    RIGHT(1) {
        @Override
        protected State apply(LinkedList<Token> stack, LinkedList<Token> buffer, Set<Arc> arcSet) {
            Token head = stack.getLast();
            Token dependent = buffer.removeFirst();
            stack.add(dependent);
            arcSet.add(new Arc(head.id, dependent.id));
            return null;
        }
    },
    SHIFT(2) {
        @Override
        protected State apply(LinkedList<Token> stack, LinkedList<Token> buffer, Set<Arc> arcSet) {
            stack.add(buffer.removeFirst());
            return null;
        }
    },
    REDUCE(3) {
        @Override
        protected State apply(LinkedList<Token> stack, LinkedList<Token> buffer, Set<Arc> arcSet) {
            stack.removeLast();
            return null;
        }
    };

    public final int index;
    public static int SIZE = values().length;

    Action(int index) {
        this.index = index;
    }

    protected abstract State apply(LinkedList<Token> stack, LinkedList<Token> buffer, Set<Arc> arcSet);

    protected static Set<Action> getActions(State state, Set<Arc> arcSet) {
        Set<Action> actions = new LinkedHashSet<>();
        if (state.bufferHead == null) {
            return actions;
        }
        if (state.stackTop != null) {
            boolean canLeft = true;
            if (state.stackTop.isRoot()) {
                canLeft = false;
            }
            for (Arc arc : arcSet) {
                if (state.stackTop.id == arc.dependent) {
                    canLeft = false;
                    actions.add(Action.REDUCE);
                    break;
                }
            }
            if (canLeft) {
                actions.add(Action.LEFT);
            }
            actions.add(Action.RIGHT);
        }
        actions.add(Action.SHIFT);
        return actions;
    }

    protected static Action getGoldAction(State state, List<Token> buffer, Set<Arc> arcSet) {
        Set<Action> actions = state.nextActions;
        Token sToken = state.stackTop;
        Token bToken = state.bufferHead;
        if (actions.contains(Action.LEFT) && sToken.head == bToken.id) {
            return Action.LEFT;
        } else if (actions.contains(Action.RIGHT) && bToken.head == sToken.id) {
            return Action.RIGHT;
        } else if (actions.contains(Action.REDUCE)) {
            Boolean valid = true;
            for (Token token : buffer) {
                // check if all dependents of sToken have already been attached
                if (token.head == sToken.id && !arcSet.contains(new Arc(sToken.id, token.id))) {
                    valid = false;
                    break;
                }
            }
            if (valid) {
                return Action.REDUCE;
            }
        }
        return Action.SHIFT;
    }
}
