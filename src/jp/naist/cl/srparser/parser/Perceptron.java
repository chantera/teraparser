package jp.naist.cl.srparser.parser;

import jp.naist.cl.srparser.io.Logger;
import jp.naist.cl.srparser.model.Feature;
import jp.naist.cl.srparser.model.Feature.Index;
import jp.naist.cl.srparser.parser.Parser.Action;

import java.util.List;
import java.util.Map;

/**
 * jp.naist.cl.srparser.parser
 *
 * @author Hiroki Teranishi
 */
public class Perceptron implements Classifier {
    public Action classify(Index[] featureIndexes, Map<Action, Map<Index, Double>> weights, List<Action> options) {
        double bestScore = -1.0;
        Action bestAction = Action.SHIFT;
        for (Action option : options) {
            double score = 0.0;
            Map<Index, Double> weight = weights.get(option);
            for (Index index : featureIndexes) {
                score += weight.get(index);
            }
            if (score > bestScore) {
                bestScore = score;
                bestAction = option;
            }
        }
        return bestAction;
    }
}
