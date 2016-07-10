package jp.naist.cl.srparser.parser;

import jp.naist.cl.srparser.model.Feature;
import jp.naist.cl.srparser.model.Feature.Index;
import jp.naist.cl.srparser.model.Sentence;
import jp.naist.cl.srparser.model.Token;
import jp.naist.cl.srparser.util.Tuple;

import java.awt.datatransfer.FlavorEvent;
import java.util.*;

/**
 * jp.naist.cl.srparser.parser
 *
 * @author Hiroki Teranishi
 */
public class Parser {
    private boolean initialized = false;
    private Classifier classifier;
    private LinkedList<Token> stack = new LinkedList<>();
    private LinkedList<Token> buffer = new LinkedList<>();
    private LinkedList<Token> output = new LinkedList<>();
    private Map<Action, Map<Index, Double>> weights;

    private LinkedList<Tuple<State, Action>> transiaions;

    public Parser() {
        this(null);
    }

    public Parser(Map<Action, Map<Index, Double>> weights) {
        if (weights == null) {
            weights = new EnumMap<>(Action.class);
            for (Action action : Action.values()) {
                weights.put(action, new LinkedHashMap<Index, Double>());
            }
        }
        this.weights = weights;
        classifier = new Perceptron();
        initialized = true;
    }

    private void reset(Token[] tokens) {
        stack.clear();
        output.clear();
        buffer = new LinkedList<>(Arrays.asList(tokens));
        transiaions = new LinkedList<>();
        Action.SHIFT.exec(this);
    }

    public Sentence parse(Sentence sentence) {
        reset(sentence.tokens);
        State state = new State(stack, buffer);
        while (buffer.size() > 0) {
            Action action = getNextAction(state);
            action.exec(this);
            transiaions.add(new Tuple<>(state, action));
            state = new State(stack, buffer);
        }
        return new Sentence(output.toArray(new Token[output.size()]));
    }

    private Action getNextAction(State state) {
        if (stack.size() == 0) {
            // throw new IllegalStateException("Initial State: stack is empty.");
            return Action.SHIFT;
        } else if (buffer.size() == 0) {
            throw new IllegalStateException("Terminal State: buffer is empty.");
        }
        List<Action> actions = new ArrayList<>();
        if (!stack.getLast().isRoot()) {
            actions.add(Action.LEFT);
        }
        actions.add(Action.RIGHT);
        actions.add(Action.SHIFT);
        return classifier.classify(state.features, weights, actions);
    }

    private void left() {
        if (stack.size() == 0 || buffer.size() == 0) {
            throw new IllegalStateException("Transition.LEFT: stack and buffer must not be empty.");
        }
        Map<Token.Attribute, String> attributes = stack.pop().cloneAttributes();
        Token modifier = buffer.getFirst();
        attributes.put(Token.Attribute.HEAD, String.valueOf(modifier.id));
        output.add(new Token(attributes));
    }

    private void right() {
        if (stack.size() == 0 || buffer.size() == 0) {
            throw new IllegalStateException("Transition.RIGHT: stack and buffer must not be empty.");
        }
        Map<Token.Attribute, String> attributes = buffer.getFirst().cloneAttributes();
        Token modifier = stack.pop();
        attributes.put(Token.Attribute.HEAD, String.valueOf(modifier.id));
        output.add(new Token(attributes));
        buffer.set(0, modifier);
    }

    private void shift() {
        if (buffer.size() == 0) {
            throw new IllegalStateException("Transition.SHIFT: buffer must not be empty.");
        }
        stack.add(buffer.pop());
    }

    private void extendWeightSize(int size) {
        int weightSize = weights.get(Action.LEFT).size();
        if (weightSize < size) {
            for (Action action : Action.values()) {
                Map<Index, Double> weight = weights.get(action);
                for (int i = weightSize; i < size + 100; i++) {
                    weight.put(new Index(i), 0.0);
                }
                weights.put(action, weight);
            }
        }
    }

    public class State {
        public final List<Token> stack;
        public final List<Token> buffer;
        public final Index[] features;

        public State(final List<Token> stack, final List<Token> buffer) {
            this.stack = stack;
            this.buffer = buffer;
            this.features = Feature.extract(stack, buffer);
            extendWeightSize(Feature.getSize());
        }
    }

    protected enum Action {
        LEFT {
            @Override
            protected void exec(Parser parser) {
                parser.left();
            }
        },
        RIGHT {
            @Override
            protected void exec(Parser parser) {
                parser.right();
            }
        },
        SHIFT {
            @Override
            protected void exec(Parser parser) {
                parser.shift();
            }
        };
        protected abstract void exec(Parser parser);
    }
}
