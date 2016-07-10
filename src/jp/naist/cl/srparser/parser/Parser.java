package jp.naist.cl.srparser.parser;

import jp.naist.cl.srparser.model.Feature;
import jp.naist.cl.srparser.model.Feature.Index;
import jp.naist.cl.srparser.model.Sentence;
import jp.naist.cl.srparser.model.Token;
import jp.naist.cl.srparser.util.Tuple;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;


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
    private Map<Action, Map<Index, Double>> weights;

    protected List<Tuple<State, Action>> transitions;

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
    }

    public void setClassifier(Classifier classifier) {
        this.classifier = classifier;
    }

    public List<Tuple<State, Action>> getTransiaions() {
        return transitions;
    }

    public Map<Action, Map<Index, Double>> getWeights() {
        return weights;
    }

    public void setWeights(Map<Action, Map<Index, Double>> weights) {
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
        Action.SHIFT.exec(this);
    }

    public Sentence parse(Sentence sentence) {
        if (classifier == null) {
            throw new IllegalStateException("Classifier must be set.");
        }
        reset(sentence.tokens);
        State state = new State(stack, buffer, arcSet);
        while (buffer.size() > 0) {
            Action action = getNextAction(state);
            action.exec(this);
            transitions.add(new Tuple<>(state, action));
            state = new State(stack, buffer, arcSet);
        }
        transitions.add(new Tuple<State, Action>(state, null));
        return new Sentence(sentence.id.getValue(), output.toArray(new Token[output.size()]));
    }

    protected Action getNextAction(State state) {
        /*
        if (state.stack.size() == 0) {
            // throw new IllegalStateException("Initial State: stack is empty.");
            return Action.SHIFT;
        } else if (state.buffer.size() == 0) {
            throw new IllegalStateException("Terminal State: buffer is empty.");
        }
        List<Action> actions = new ArrayList<>();
        if (!state.stack.getLast().isRoot()) {
            actions.add(Action.LEFT);
        }
        actions.add(Action.RIGHT);
        actions.add(Action.SHIFT);
        */
        Set<Action> actions = getPossibleAction(state);
        if (actions.size() == 0) {
            throw new IllegalStateException("Any action is not permitted.");
        } else if (actions.size() == 1) {
            return (Action) actions.iterator();
        }
        return classifier.classify(state.features, weights, actions);
    }

    protected Action getGoldAction(State state) {
        /*
        if (state.stack.size() == 0) {
            return Action.SHIFT;
        } else if (state.buffer.size() == 0) {
            throw new IllegalStateException("Terminal State: buffer is empty.");
        }
        */
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
        return getPossibleAction(state.stack, state.buffer, state.arcSet);
    }

    private Set<Action> getPossibleAction(LinkedList<Token> stack, LinkedList<Token> buffer, Set<Arc> arcSet) {
        Set<Action> actions = new LinkedHashSet<>();
        if (buffer.size() == 0) {
            return actions;
        }
        int stackSize = stack.size();
        if (stackSize > 0) {
            boolean canLeft = true;
            boolean canReduce = false;
            Token sLast = stack.getLast();
            if (sLast.isRoot()) {
                canLeft = false;
            }
            for (Arc arc : arcSet) {
                if (sLast.id == arc.dependent) {
                    canLeft = false;
                    canReduce = true;
                    break;
                }
            }
            if (canLeft) {
                actions.add(Action.LEFT);
            }
            actions.add(Action.RIGHT);
            if (canReduce) {
                actions.add(Action.REDUCE);
            }
        }
        actions.add(Action.SHIFT);
        return actions;
    }

    private void left() {
        if (stack.size() == 0 || buffer.size() == 0) {
            throw new IllegalStateException("Transition.LEFT: stack and buffer must not be empty.");
        }
        Token modifier = buffer.getFirst();
        Token dependent = Token.clone(stack.removeLast(), modifier.id);
        arcSet.add(new Arc(modifier.id, dependent.id));
        output.add(dependent);
    }

    private void right() {
        if (stack.size() == 0 || buffer.size() == 0) {
            throw new IllegalStateException("Transition.RIGHT: stack and buffer must not be empty.");
        }
        Token modifier = stack.getLast();
        Token dependent = Token.clone(buffer.removeFirst(), modifier.id);
        stack.add(dependent);
        arcSet.add(new Arc(modifier.id, dependent.id));
        output.add(dependent);
    }

    private void shift() {
        if (buffer.size() == 0) {
            throw new IllegalStateException("Transition.SHIFT: buffer must not be empty.");
        }
        stack.add(buffer.removeFirst());
    }

    private void reduce() {
        /*
        if (stack.size() == 0 || buffer.size() == 0) {
            throw new IllegalStateException("Transition.REDUCE: stack and buffer must not be empty.");
        } else if () {

        }
        */
        stack.removeLast();
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

    public class Arc extends Tuple<Integer, Integer> {
        private final int head;
        private final int dependent;

        public Arc(Integer head, Integer dependent) {
            super(head, dependent);
            this.head = super.left;
            this.dependent = super.right;
        }
    }

    public class State {
        public final LinkedList<Token> stack;
        public final LinkedList<Token> buffer;
        public final Set<Arc> arcSet;
        public final Index[] features;

        public State(final LinkedList<Token> stack, final LinkedList<Token> buffer, final Set<Arc> arcSet) {
            this.stack = stack;
            this.buffer = buffer;
            this.arcSet = arcSet;
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
        },
        REDUCE {
            @Override
            protected void exec(Parser parser) {
                parser.reduce();
            }
        };
        protected abstract void exec(Parser parser);
    }
}
