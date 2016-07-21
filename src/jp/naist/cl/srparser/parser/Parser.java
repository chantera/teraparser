package jp.naist.cl.srparser.parser;

import jp.naist.cl.srparser.model.Sentence;
import jp.naist.cl.srparser.transition.State;

/**
 * jp.naist.cl.srparser.parser
 *
 * @author Hiroki Teranishi
 */
public abstract class Parser {
    Perceptron classifier;

    public Parser(Perceptron classifier) {
        this.classifier = classifier;
    }

    public Perceptron getClassifier() {
        return classifier;
    }

    public abstract State parse(Sentence sentence);
}
