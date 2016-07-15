package jp.naist.cl.srparser.parser;

import jp.naist.cl.srparser.util.Tuple;

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

    public static int[][] update(int[][] weights, State oracle, State predict) {
        /*
        while (!state.isInitial()) {
            State prevState = state.prevState;
            if (state.prevAction != prevState.goldAction) {
                increase(weights[prevState.goldAction.index], prevState.features);
                decrease(weights[state.prevAction.index], prevState.features);
            }
            state = prevState;
        }
        */
        State prevState;
        while ((prevState = oracle.prevState) != null) {
            increase(weights[oracle.prevAction.index], prevState.features);
            oracle = prevState;
        }
        while ((prevState = predict.prevState) != null) {
            decrease(weights[predict.prevAction.index], prevState.features);
            predict = prevState;
        }
        return weights;
    }

    /*
    public static int[][] update(int[][] weights, Action[] oracleActions, Action[] predictActions) {
        for ()

        State prevState;
        while ((prevState = state.prevState) != null) {
            if (state.prevAction != Oracle.get(prevState)) {
                increase(weights[Oracle.get(prevState).index], prevState.features);
                decrease(weights[state.prevAction.index], prevState.features);
            }
            state = prevState;
        }
        return weights;
    }
    */

    public static void decrease(int[] weight, int[] featureIndexes) {
        updateWeight(weight, featureIndexes, -1);
    }

    public static void increase(int[] weight, int[] featureIndexes) {
        updateWeight(weight, featureIndexes, 1);
    }

    public static void updateWeight(int[] weight, int[] featureIndexes, int value) {
        for (int feature : featureIndexes) {
            weight[feature] += value;
        }
    }
}
