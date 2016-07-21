package jp.naist.cl.srparser.parser;

import jp.naist.cl.srparser.model.Sentence;
import jp.naist.cl.srparser.transition.Action;
import jp.naist.cl.srparser.transition.State;

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
            Action action = classifier.getNextAction(state);
            state = action.apply(state);
        }
        return state;
    }
}
