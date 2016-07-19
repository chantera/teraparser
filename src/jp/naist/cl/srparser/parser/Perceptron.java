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
    private float[][] weights;
    private float[][] averagedWeights;

    private int iteration;

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
        this.iteration = 1;
    }

    public float[][] getAveragedWeights() {
        float[][] result = new float[weights.length][weights[0].length];
        for (int i = 0; i < weights.length; i++) {
            for (int j = 0; j < weights[i].length; j++) {
                result[i][j] = weights[i][j] - averagedWeights[i][j] / iteration;
            }
        }
        return result;
    }

    public void incrementIteration() {
        iteration++;
    }

    public Action classify(int[] featureIndexes, Collection<Action> actions) {
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

    void update(State oracle, State predict) {
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
    }

    private void updateWeight(float[] weight, int[] featureIndexes, float value) {
        for (int feature : featureIndexes) {
            weight[feature] += value;
        }
    }
}
