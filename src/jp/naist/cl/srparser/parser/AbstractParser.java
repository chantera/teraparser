package jp.naist.cl.srparser.parser;

import jp.naist.cl.srparser.model.Sentence;
import jp.naist.cl.srparser.transition.State;

/**
 * jp.naist.cl.srparser.parser
 *
 * @author Hiroki Teranishi
 */
public abstract class AbstractParser {
    Classifier classifier;
    int[][] weights;

    public AbstractParser(int[][] weights) {
        setWeights(weights);
    }

    public abstract State parse(Sentence sentence);

    public void setClassifier(Classifier classifier) {
        this.classifier = classifier;
    }

    public int[][] getWeights() {
        return weights;
    }

    public void setWeights(int[][] weights) {
        this.weights = weights;
    }
}
