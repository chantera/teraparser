package jp.teraparser.parser;

import jp.teraparser.model.Sentence;
import jp.teraparser.transition.Action;
import jp.teraparser.transition.State;

/**
 * jp.teraparser.parser
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
