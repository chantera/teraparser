package jp.naist.cl.srparser.parser;

import jp.naist.cl.srparser.io.Logger;
import jp.naist.cl.srparser.model.Feature;
import jp.naist.cl.srparser.model.Sentence;
import jp.naist.cl.srparser.model.Token;
import jp.naist.cl.srparser.util.Tuple;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

/**
 * jp.naist.cl.srparser.parser
 *
 * @author Hiroki Teranishi
 */
public class Trainer extends Parser {
    private Sentence[] sentences;
    private Map<Sentence.ID, Set<Arc>> goldArcSets = new LinkedHashMap<>();
    // private Map<Sentence.ID, State> goldState = new LinkedHashMap<>();
    private Map<Sentence.ID, List<Tuple<State, Action>>> goldTransitions = new LinkedHashMap<>();

    public Trainer(Sentence[] sentences) {
        super(new int[Action.SIZE][Feature.SIZE], new Perceptron());
        this.sentences = sentences;
        for (Sentence sentence : sentences) {
            goldArcSets.put(sentence.id, parseGold(sentence));
            // goldState.put(sentence.id, state);
            goldTransitions.put(sentence.id, transitions);
        }
        setWeights(new int[Action.SIZE][Feature.SIZE]);
    }

    public void train() {
        train(null);
    }

    public void train(TrainCallback callback) {
        Map<Sentence.ID, Set<Arc>> predArcSets = new LinkedHashMap<>();
        for (Sentence sentence : sentences) {
            predArcSets.put(sentence.id, parse(sentence));
            // setWeights(Perceptron.update(weights, goldState.get(sentence.id), state));
            for (Tuple<State, Action> v : goldTransitions.get(sentence.id)) {
            }
            setWeights(Perceptron.update(weights, goldTransitions.get(sentence.id), transitions));
        }
        if (callback != null) {
            callback.accept(goldArcSets, predArcSets);
        }
    }

    private Set<Arc> parseGold(Sentence sentence) {
        reset(sentence.tokens);
        while (!state.isTerminal()) {
            Action action = getGoldAction(state);
            action.apply(stack, buffer, arcSet);
            transitions.add(new Tuple<>(state, action));
            state = State.from(state, action).createNext(stack, buffer, arcSet);
        }
        transitions.add(new Tuple<>(state, null));
        return arcSet;
    }

    private Action getGoldAction(State state) {
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

    public interface TrainCallback extends BiConsumer<Map<Sentence.ID, Set<Arc>>, Map<Sentence.ID, Set<Arc>>> {}
}
