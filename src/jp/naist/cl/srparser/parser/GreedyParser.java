package jp.naist.cl.srparser.parser;

import jp.naist.cl.srparser.model.Sentence;
import jp.naist.cl.srparser.transition.Action;
import jp.naist.cl.srparser.transition.State;

/**
 * jp.naist.cl.srparser.parser
 *
 * @author Hiroki Teranishi
 */
public class GreedyParser extends Parser {

    public GreedyParser(Perceptron classifier) {
        super(classifier);
    }

    @Override
    public State parse(Sentence sentence) {
        State state = new State(sentence);
        while (!state.isTerminal()) {
            Action action = classifier.getNextAction(state); // retrieve an one best action greedily
            state = action.apply(state);
        }
        return state;
    }
}
