package jp.naist.cl.srparser.parser;

/**
 * jp.naist.cl.srparser.parser
 *
 * @author Hiroki Teranishi
 */
public abstract class Classifier {
    public abstract Parser.Transition classify(double[] w, double[] x);
}
