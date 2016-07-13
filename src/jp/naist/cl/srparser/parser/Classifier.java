package jp.naist.cl.srparser.parser;

import jp.naist.cl.srparser.model.Feature;

import java.util.Collection;
import java.util.Map;

/**
 * jp.naist.cl.srparser.parser
 *
 * @author Hiroki Teranishi
 */
public interface Classifier {
    public Action classify(Feature.Index[] featureIndexes, Map<Action, Map<Feature.Index, Double>> weights, Collection<Action> options);
}
