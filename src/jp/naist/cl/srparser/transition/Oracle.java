package jp.naist.cl.srparser.transition;

import jp.naist.cl.srparser.model.Sentence;
import jp.naist.cl.srparser.model.Token;

import java.util.HashMap;
import java.util.Set;

/**
 * jp.naist.cl.srparser.transition
 *
 * @author Hiroki Teranishi
 */
public class Oracle {
    private final Algorithm algorithm;
    private final HashMap<Sentence.ID, State> oracleRegistry = new HashMap<>();

    public Oracle(Algorithm algorithm) {
        this.algorithm = algorithm;
    }

    public State getState(Sentence sentence) {
        State state = oracleRegistry.getOrDefault(sentence.id, null);
        if (state == null) {
            state = new State(sentence);
            while (!state.isTerminal()) {
                Action action = algorithm.getAction(state);
                state = action.apply(state);
            }
            oracleRegistry.put(sentence.id, state);
        }
        return state;
    }

    public enum Algorithm {
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
        protected abstract Action getAction(State state);
    }
}
