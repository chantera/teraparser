package jp.teraparser.parser;

import jp.teraparser.model.Sentence;
import jp.teraparser.transition.State;

/**
 * jp.teraparser.parser
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
