package jp.naist.cl.srparser.parser;

import jp.naist.cl.srparser.model.Feature;
import jp.naist.cl.srparser.model.Sentence;
import jp.naist.cl.srparser.model.Token;
import jp.naist.cl.srparser.util.Tuple;

import java.util.*;


/**
 * jp.naist.cl.srparser.parser
 *
 * @author Hiroki Teranishi
 */
public class Parser {
    private Classifier classifier;
    protected Token[] tokens;
    protected LinkedList<Token> stack;
    protected LinkedList<Token> buffer;
    protected Set<Arc> arcSet;
    protected LinkedList<Token> output;
    private int[][] weights;

    protected List<Tuple<State, Action>> transitions;

    public Parser() {
        this(null);
    }

    public Parser(int[][] weights) {
        if (weights == null) {
            weights = new int[Action.SIZE][Feature.SIZE];
        }
        this.weights = weights;
    }

    public void setClassifier(Classifier classifier) {
        this.classifier = classifier;
    }

    public List<Tuple<State, Action>> getTransiaions() {
        return transitions;
    }

    public int[][] getWeights() {
        return weights;
    }

    public void setWeights(int[][] weights) {
        this.weights = weights;
    }

    public Set<Arc> getArcSet() {
        return arcSet;
    }

    protected void reset(Token[] tokens) {
        this.tokens = tokens;
        stack = new LinkedList<>();
        buffer = new LinkedList<>(Arrays.asList(tokens));
        arcSet = new LinkedHashSet<>();
        output = new LinkedList<>();
        transitions = new LinkedList<>();
        Action.SHIFT.apply(stack, buffer, arcSet);
    }

    public Sentence parse(Sentence sentence) {
        if (classifier == null) {
            throw new IllegalStateException("Classifier must be set.");
        }
        reset(sentence.tokens);
        State state = new State(stack, buffer, arcSet);
        while (buffer.size() > 0) {
            Action action = getNextAction(state);
            action.apply(stack, buffer, arcSet);
            transitions.add(new Tuple<>(state, action));
            state = new State(stack, buffer, arcSet);
        }
        transitions.add(new Tuple<>(state, null));
        return new Sentence(sentence.id.getValue(), output.toArray(new Token[output.size()]));
    }

    protected Action getNextAction(State state) {
        Set<Action> actions = getPossibleAction(state);
        if (actions.size() == 0) {
            throw new IllegalStateException("Any action is not permitted.");
        } else if (actions.size() == 1) {
            return (Action) actions.iterator();
        }
        return classifier.classify(state.features, weights, actions);
    }

    protected Action getGoldAction(State state) {
        Set<Action> actions = getPossibleAction(state);

        Token sToken = state.stack.getLast();
        Token bToken = state.buffer.getFirst();
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

    private Set<Action> getPossibleAction(State state) {
        return Action.getActions(state.stack, state.buffer, state.arcSet);
    }
}
