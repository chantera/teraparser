package jp.naist.cl.srparser.parser;

import jp.naist.cl.srparser.model.Sentence;
import jp.naist.cl.srparser.model.Token;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;

/**
 * jp.naist.cl.srparser.parser
 *
 * @author Hiroki Teranishi
 */
public class Parser extends AbstractParser {
    protected LinkedList<Token> stack;
    protected LinkedList<Token> buffer;
    protected Set<Arc> arcSet;
    protected State state;

    public Parser(int[][] weights, Classifier classifier) {
        super(weights);
        setClassifier(classifier);
    }

    protected void reset(Token[] tokens) {
        stack  = new LinkedList<>();
        buffer = new LinkedList<>(Arrays.asList(tokens));
        arcSet = new LinkedHashSet<>();
        Action.SHIFT.apply(stack, buffer, arcSet);
        state  = new State(stack, buffer, arcSet);
    }

    @Override
    public Set<Arc> parse(Sentence sentence) {
        reset(sentence.tokens);
        while (!state.isTerminal()) {
            Action action = getNextAction(state);
            action.apply(stack, buffer, arcSet);
            state = State.from(state, action).createNext(stack, buffer, arcSet);
        }
        return arcSet;
    }

    protected Action getNextAction(State state) {
        Set<Action> actions = state.nextActions;
        if (actions.size() == 0) {
            throw new IllegalStateException("Any action is not permitted.");
        } else if (actions.size() == 1) {
            return (Action) actions.iterator();
        }
        return classifier.classify(state.features, weights, actions);
    }
}
