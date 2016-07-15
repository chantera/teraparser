package jp.naist.cl.srparser.parser;

import jp.naist.cl.srparser.model.Token;

import java.util.*;

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

        /**
         * Left
         * (s|i, j|b, A) => (s, j|b, A+(j,l,i))
         */
        @Override
        protected State apply(State state) {
            ArrayDeque<Integer> stack = state.stack.clone();
            Token head = state.getBufferHeadToken();
            Token dependent = state.tokens[stack.pop()];
            return new State(state, this, new Arc(head.id, dependent.id), stack, state.bufferHead);
        }

        // @Override
        protected Boolean applicable(State state) {
            return !state.isTerminal();
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

        /**
         * Right
         * (s|i, j|b, A) => (s|ij, b, A+(i,l,j))
         */
        @Override
        protected State apply(State state) {
            ArrayDeque<Integer> stack = state.stack.clone();
            Token head = state.getStackTopToken();
            Token dependent = state.getBufferHeadToken();
            stack.push(state.bufferHead);
            return new State(state, this, new Arc(head.id, dependent.id), stack, state.bufferHead + 1);
        }
    },
    SHIFT(2) {
        @Override
        protected State apply(LinkedList<Token> stack, LinkedList<Token> buffer, Set<Arc> arcSet) {
            stack.add(buffer.removeFirst());
            return null;
        }

        /**
         * Shift
         * (s, i|b, A) => (s|i, b, A)
         */
        @Override
        protected State apply(State state) {
            ArrayDeque<Integer> stack = state.stack.clone();
            stack.push(state.bufferHead);
            return new State(state, this, null, stack, state.bufferHead + 1);
        }
    },
    REDUCE(3) {
        @Override
        protected State apply(LinkedList<Token> stack, LinkedList<Token> buffer, Set<Arc> arcSet) {
            stack.removeLast();
            return null;
        }

        /**
         * Reduce
         * (s|i, b, A) => (s, b, A)
         */
        @Override
        protected State apply(State state) {
            ArrayDeque<Integer> stack = state.stack.clone();
            stack.pop();
            return new State(state, this, null, stack, state.bufferHead);
        }
    };

    public final int index;
    public static int SIZE = values().length;

    Action(int index) {
        this.index = index;
    }

    protected abstract State apply(LinkedList<Token> stack, LinkedList<Token> buffer, Set<Arc> arcSet);

    protected abstract State apply(State state);

    //protected abstract Boolean applicable(State state);

    protected static Set<Action> getPossibleActions(State state) {
        Set<Action> actions = new HashSet<>();
        if (state.isTerminal()) {
            return actions;
        }
        Token stackTop = state.getStackTopToken();
        if (state.stack.getFirst() != null) {
            boolean canLeft = true;
            if (stackTop.isRoot()) {
                canLeft = false;
            }
            if (state.hasDependentArc(stackTop.id)) {
                canLeft = false;
                actions.add(Action.REDUCE);
            }
            /*
            for (Arc arc : state.getArcSet()) {
                if (stackTop.id == arc.dependent) {
                    canLeft = false;
                    actions.add(Action.REDUCE);
                    break;
                }
            }
            */
            if (canLeft) {
                actions.add(Action.LEFT);
            }
            actions.add(Action.RIGHT);
        }
        actions.add(Action.SHIFT);
        return actions;
    }

    /*
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
    */

    /**
     * Standard oracle for arc-eager dependency parsing
     *
     * Goldberg, Y., & Nivre, J. (2012).
     * A Dynamic Oracle for Arc-Eager Dependency Parsing.
     * Proceedings of the 24th International Conference on Computational Linguistics (COLING), 2(December), 959–976.
     *
     * ================ Algorithm ================
     * 1: if c = (σ|i, j|β,A) and (j, l, i) ∈ A_gold then
     * 2:     t ← LEFT-ARC_l
     * 3: else if c = (σ|i, j|β,A) and (i, l, j) ∈ A_gold then
     * 4:     t ← RIGHT-ARC_l
     * 5: else if c = (σ|i, j|β,A) and ∃k[k < i ∧ ∃l[(k, l, j) ∈ A_gold ∨ (j, l, k) ∈ A_gold]] then
     * 6:     t ← REDUCE
     * 7: else
     * 8:     t ← SHIFT
     * 9: return t
     * -------------------------------------------
     */
    // protected static Action getStaticOracle(State state) {
    //     Set<Action> actions = state.possibleActions;
    //     Token sToken = state.getStackTopToken();
    //     Token bToken = state.getBufferHeadToken();
    //     if (actions.contains(Action.LEFT) && sToken.head == bToken.id) {
    //         return Action.LEFT;
    //     } else if (actions.contains(Action.RIGHT) && bToken.head == sToken.id) {
    //         return Action.RIGHT;
    //     } else if (actions.contains(Action.REDUCE)) {
    //         for (int i = 0; i < state.stack.getFirst(); i++) {
    //             if (state.hasArc(i, bToken.id) || state.hasArc(bToken.id, i)) {
    //                 return Action.REDUCE;
    //             }
    //         }
    //         /*
    //         Boolean valid = true;
    //         for (int i = state.bufferHead; i < state.bufferSize; i++) {
    //             // check if all dependents of sToken have already been attached
    //             if (state.tokens[i].head == sToken.id) { // && !arcSet.contains(new Arc(sToken.id, token.id))) {
    //                 valid = false;
    //                 break;
    //             }
    //         }
    //         if (valid) {
    //             return Action.REDUCE;
    //         }
    //         */
    //     } else {
    //         return Action.SHIFT;
    //     }
    // }
}
