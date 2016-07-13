package jp.naist.cl.srparser.parser;

import jp.naist.cl.srparser.model.Feature;

import java.util.Collection;

/**
 * jp.naist.cl.srparser.parser
 *
 * @author Hiroki Teranishi
 */
public interface Classifier {
    public Action classify(Feature.Index[] featureIndexes, int[][] weights, Collection<Action> options);
}
