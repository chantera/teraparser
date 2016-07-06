package jp.naist.cl.srparser.parser;

import jp.naist.cl.srparser.model.Sentence;
import jp.naist.cl.srparser.model.Token;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Map;

/**
 * jp.naist.cl.srparser.parser
 *
 * @author Hiroki Teranishi
 */
public class Parser {
    private boolean initialized = false;
    private Classifier classifier;
    private LinkedList<Token> stack;
    private LinkedList<Token> buffer;
    private LinkedList<Token> output;
    private double[] weights;

    public Parser() {
        this(new double[10]);
    }

    public Parser(double[] weights) {
        initialize(weights);
    }

    private void initialize(double[] weights) {
        if (initialized) {
            return;
        }
        this.weights = weights;
        classifier = new SVMClassifier();
        stack = new LinkedList<>();
        buffer = new LinkedList<>();
        output = new LinkedList<>();
        initialized = true;
    }

    private void reset(Token[] tokens) {
        stack.clear();
        output.clear();
        buffer = new LinkedList<>(Arrays.asList(tokens));
    }

    public Sentence parse(Sentence sentence) {
        reset(sentence.tokens);
        while (buffer.size() > 0) {
            getNextTransition().exec(this);
        }
        return new Sentence(output.toArray(new Token[output.size()]));
    }

    private Transition getNextTransition() {
        if (stack.size() == 0) {
            return Transition.SHIFT;
        }
        return classifier.classify(new double[]{1.0}, new double[]{1.0});
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

    protected enum Transition {
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
