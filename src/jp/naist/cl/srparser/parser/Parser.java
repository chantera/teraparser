package jp.naist.cl.srparser.parser;

import jp.naist.cl.srparser.model.Sentence;
import jp.naist.cl.srparser.transition.Action;
import jp.naist.cl.srparser.transition.State;

import java.util.Set;


/**
 * jp.naist.cl.srparser.parser
 *
 * @author Hiroki Teranishi
 */
public class Parser {
    Perceptron classifier;

    public Parser(Perceptron classifier) {
        this.classifier = classifier;
    }

    public State parse(Sentence sentence) {
        State state = new State(sentence);
        while (!state.isTerminal()) {
            Action action = getNextAction(state);
            state = action.apply(state);
        }
        return state;
    }

    private Action getNextAction(State state) {
        Set<Action> actions = state.possibleActions;
        if (actions.size() == 0) {
            throw new IllegalStateException("Any action is not permitted.");
        } else if (actions.size() == 1) {
            return (Action) actions.iterator();
        }
        return classifier.classify(state.features, actions);
    }
}
