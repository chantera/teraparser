package jp.naist.cl.srparser.parser;

import jp.naist.cl.srparser.model.Feature;

import java.util.Collection;
import java.util.Map;

/**
 * jp.naist.cl.srparser.parser
 *
 * @author Hiroki Teranishi
 */
public class SVM implements Classifier {
    @Override
    public Parser.Action classify(Feature.Index[] featureIndexes, Map<Parser.Action, Map<Feature.Index, Double>> weights, Collection<Parser.Action> options) {
        return Parser.Action.LEFT;
    }
}
