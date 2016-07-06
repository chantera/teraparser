package jp.naist.cl.srparser.parser;

/**
 * jp.naist.cl.srparser.parser
 *
 * @author Hiroki Teranishi
 */
public class SVMClassifier extends Classifier {
    @Override
    public Parser.Transition classify(double[] w, double[] x) {
        return Parser.Transition.LEFT;
    }
}
