package jp.naist.cl.srparser.parser;

import jp.naist.cl.srparser.model.Sentence;

import java.util.Set;

/**
 * jp.naist.cl.srparser.parser
 *
 * @author Hiroki Teranishi
 */
public abstract class AbstractParser {
    protected Classifier classifier;
    protected int[][] weights;

    public AbstractParser(int[][] weights) {
        setWeights(weights);
    }

    public abstract Arc[] parse(Sentence sentence);

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
