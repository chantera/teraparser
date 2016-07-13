package jp.naist.cl.srparser.parser;

import jp.naist.cl.srparser.model.Feature.Index;
import jp.naist.cl.srparser.util.Tuple;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * jp.naist.cl.srparser.parser
 *
 * @author Hiroki Teranishi
 */
public class Perceptron implements Classifier {
    public Action classify(Index[] featureIndexes, Map<Action, Map<Index, Double>> weights, Collection<Action> options) {
        double bestScore = -1.0;
        Action bestAction = Action.SHIFT;
        for (Action option : options) {
            double score = 0.0;
            Map<Index, Double> weight = weights.get(option);
            for (Index index : featureIndexes) {
                score += weight.getOrDefault(index, 0.0);
            }
            if (score > bestScore) {
                bestScore = score;
                bestAction = option;
            }
        }
        return bestAction;
    }

    public static Map<Action, Map<Index, Double>> update(Map<Action, Map<Index, Double>> weights, List<Tuple<State, Action>> golds, List<Tuple<State, Action>> predicts) {
        /*
        for (Tuple<State, Action> gold : golds) {
            for (Feature.Index index : gold.getLeft().features) {
                System.out.println(weights.get(Action.LEFT).get(index));
                System.out.println(weights.get(Action.RIGHT).get(index));
                System.out.println(weights.get(Action.SHIFT).get(index));
            }
        }
        System.err.println("CHECK WEIGHTS");
        */

        for (int i = 0; i < predicts.size() - 1; i++) {
            Action predictAction = predicts.get(i).getRight();
            if (i >= golds.size() - 1) {
                weights.put(predictAction, decrease(weights.get(predictAction), predicts.get(i).getLeft().features));
            } else {
                Action goldAction = golds.get(i).getRight();
                if (predictAction != goldAction) {
                    weights.put(predictAction, decrease(weights.get(predictAction), predicts.get(i).getLeft().features));
                    weights.put(goldAction, increase(weights.get(goldAction), golds.get(i).getLeft().features));
                }
            }
        }
        return weights;
    }

    public static Map<Index, Double> decrease(Map<Index, Double> weight, Index[] featureIndexes) {
        return updateWeight(weight, featureIndexes, -1.0);
    }

    public static Map<Index, Double> increase(Map<Index, Double> weight, Index[] featureIndexes) {
        return updateWeight(weight, featureIndexes, 1.0);
    }

    public static Map<Index, Double> updateWeight(Map<Index, Double> weight, Index[] featureIndexes, double value) {
        for (Index index : featureIndexes) {
            weight.put(index, weight.getOrDefault(index, 0.0) + value);
        }
        return weight;
    }
}
