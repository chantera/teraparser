package jp.naist.cl.srparser.parser;

import jp.naist.cl.srparser.transition.Action;
import jp.naist.cl.srparser.transition.State;

import java.util.Collection;
import java.util.Iterator;

/**
 * jp.naist.cl.srparser.parser
 *
 * @author Hiroki Teranishi
 */
public class Perceptron implements Classifier {
    private float[][] weights;
    private float[][] averagedWeights;

    private int count;

    public Perceptron(float[][] weights) {
        setWeights(weights);
    }

    @Override
    public float[][] getWeights() {
        return weights;
    }

    @Override
    public void setWeights(float[][] weights) {
        this.weights = weights;
        this.averagedWeights = weights.clone();
        this.count = 1;
    }

    public float[][] getAveragedWeights() {
        float[][] result = new float[weights.length][weights[0].length];
        for (int i = 0; i < weights.length; i++) {
            for (int j = 0; j < weights[i].length; j++) {
                result[i][j] = weights[i][j] - averagedWeights[i][j] / count;
            }
        }
        return result;
    }

    public void incrementCount() {
        count++;
    }

    public Action classify(int[] featureIndexes, Collection<Action> actions) {
        double bestScore = Integer.MIN_VALUE;
        Action bestAction = null;
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

    void update(State oracle, State predict) {
        /*
        State prevState;
        while ((prevState = oracle.prevState) != null) {
            updateWeight(weights[oracle.prevAction.index], prevState.features, 1);
            updateWeight(averagedWeights[oracle.prevAction.index], prevState.features, iteration);
            oracle = prevState;
        }
        while ((prevState = predict.prevState) != null) {
            updateWeight(weights[predict.prevAction.index], prevState.features, -1);
            updateWeight(averagedWeights[predict.prevAction.index], prevState.features, -iteration);
            predict = prevState;
        }
        */
        State.StateIterator iterator = predict.getIterator();
        predict = iterator.next(); // initial state
        while (iterator.hasNext()) {
            predict = iterator.next();
            updateWeight(weights[predict.prevAction.index], predict.prevState.features, -1);
            updateWeight(averagedWeights[predict.prevAction.index], predict.prevState.features, -count);
        }
        iterator = oracle.getIterator();
        oracle = iterator.next(); // initial state
        while (iterator.hasNext()) {
            oracle = iterator.next();
            updateWeight(weights[oracle.prevAction.index], oracle.prevState.features, 1);
            updateWeight(averagedWeights[oracle.prevAction.index], oracle.prevState.features, count);
        }
    }

    void update(Action oracleAction, Action predictAction, int[] features) {
        updateWeight(weights[oracleAction.index], features, 1);
        updateWeight(averagedWeights[oracleAction.index], features, count);
        updateWeight(weights[predictAction.index], features, -1);
        updateWeight(averagedWeights[predictAction.index], features, -count);
    }

    private void updateWeight(float[] weight, int[] featureIndexes, float value) {
        for (int feature : featureIndexes) {
            weight[feature] += value;
        }
    }
}
