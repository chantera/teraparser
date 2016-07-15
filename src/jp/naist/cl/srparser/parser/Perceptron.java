package jp.naist.cl.srparser.parser;

import jp.naist.cl.srparser.transition.Action;
import jp.naist.cl.srparser.transition.State;

import java.util.Collection;

/**
 * jp.naist.cl.srparser.parser
 *
 * @author Hiroki Teranishi
 */
public class Perceptron implements Classifier {

    public Action classify(int[] featureIndexes, int[][] weights, Collection<Action> actions) {
        double bestScore = -1.0;
        Action bestAction = Action.SHIFT;
        for (Action action : actions) {
            double score = 0.0;
            for (int feature : featureIndexes) {
                score += weights[action.index][feature];
            }
            if (score > bestScore) {
                bestScore = score;
                bestAction = action;
            }
        }
        return bestAction;
    }

    static int[][] update(int[][] weights, State oracle, State predict) {
        State prevState;
        while ((prevState = oracle.prevState) != null) {
            updateWeight(weights[oracle.prevAction.index], prevState.features, 1);
            oracle = prevState;
        }
        while ((prevState = predict.prevState) != null) {
            updateWeight(weights[predict.prevAction.index], prevState.features, -1);
            predict = prevState;
        }
        return weights;
    }

    private static void updateWeight(int[] weight, int[] featureIndexes, int value) {
        for (int feature : featureIndexes) {
            weight[feature] += value;
        }
    }
}
