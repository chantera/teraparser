package jp.naist.cl.srparser.parser;

import jp.naist.cl.srparser.model.Token;

import java.util.LinkedList;
import java.util.Set;

/**
 * jp.naist.cl.srparser.parser
 *
 * @author Hiroki Teranishi
 */
public enum Oracle {
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
    STATIC {
        @Override
        protected Action getAction(State state) {
            Set<Action> actions = state.possibleActions;
            Token sToken = state.getStackTopToken();
            Token bToken = state.getBufferHeadToken();
            if (actions.contains(Action.LEFT) && sToken.head == bToken.id) {
                return Action.LEFT;
            } else if (actions.contains(Action.RIGHT) && bToken.head == sToken.id) {
                return Action.RIGHT;
            } else if (actions.contains(Action.REDUCE)) {
                for (int i = 0; i < state.stack.getFirst(); i++) {
                    if (state.hasArc(i, bToken.id) || state.hasArc(bToken.id, i)) {
                        return Action.REDUCE;
                    }
                }
            }
            return Action.SHIFT;
        }
    };

    /*
    private static Oracle oracle = STATIC;

    public static Action get(State state) {
        return oracle.getAction(state);
    }
    */

    public static Action get(State state) {
        return STATIC.getAction(state);
    }

    protected abstract Action getAction(State state);
}
