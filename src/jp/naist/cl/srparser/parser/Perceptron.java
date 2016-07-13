package jp.naist.cl.srparser.parser;

import jp.naist.cl.srparser.model.Feature.Index;
import jp.naist.cl.srparser.util.Tuple;

import java.util.Collection;
import java.util.List;

/**
 * jp.naist.cl.srparser.parser
 *
 * @author Hiroki Teranishi
 */
public class Perceptron implements Classifier {
    public Action classify(Index[] featureIndexes, int[][] weights, Collection<Action> options) {
        double bestScore = -1.0;
        Action bestAction = Action.SHIFT;
        for (Action option : options) {
            double score = 0.0;
            for (Index index : featureIndexes) {
                score += weights[option.index][index.getValue()];
            }
            if (score > bestScore) {
                bestScore = score;
                bestAction = option;
            }
        }
        return bestAction;
    }

    public static int[][] update(int[][] weights, List<Tuple<State, Action>> golds, List<Tuple<State, Action>> predicts) {
        for (int i = 0; i < predicts.size() - 1; i++) {
            Action predictAction = predicts.get(i).getRight();
            if (i >= golds.size() - 1) {
                decrease(weights[predictAction.index], predicts.get(i).getLeft().features);
            } else {
                Action goldAction = golds.get(i).getRight();
                if (predictAction != goldAction) {
                    decrease(weights[predictAction.index], predicts.get(i).getLeft().features);
                    increase(weights[goldAction.index], golds.get(i).getLeft().features);
                }
            }
        }
        return weights;
    }

    public static void decrease(int[] weight, Index[] featureIndexes) {
        updateWeight(weight, featureIndexes, -1);
    }

    public static void increase(int[] weight, Index[] featureIndexes) {
        updateWeight(weight, featureIndexes, 1);
    }

    public static void updateWeight(int[] weight, Index[] featureIndexes, int value) {
        for (Index index : featureIndexes) {
            weight[index.getValue()] += value;
        }
    }
}
