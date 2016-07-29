package jp.naist.cl.srparser.parser;

import jp.naist.cl.srparser.model.Sentence;
import jp.naist.cl.srparser.transition.Oracle;
import jp.naist.cl.srparser.transition.State;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

/**
 * jp.naist.cl.srparser.parser
 *
 * @author Hiroki Teranishi
 */
public class StructuredLearningTrainer extends Trainer implements BeamSearchDecoder {
    private CompletionService<List<BeamItem>> completionService;
    private final int beamWidth;
    private boolean earlyUpdate;
    private Consumer<Sentence> updateMethod;

    public StructuredLearningTrainer(Sentence[] sentences, Oracle oracle, ExecutorService executor, int beamWidth) {
        this(sentences, oracle, executor, beamWidth, false); // default: earlyUpdate = false
    }

    public StructuredLearningTrainer(Sentence[] sentences, Oracle oracle, ExecutorService executor, int beamWidth, boolean earlyUpdate) {
        super(new BeamSearchParser(new Perceptron(), executor, beamWidth), oracle, sentences);
        completionService = new ExecutorCompletionService<>(executor);
        this.beamWidth = beamWidth;
        this.earlyUpdate = earlyUpdate;
        if (earlyUpdate) {
            updateMethod = this::trainWithEarlyUpdate;
        } else {
            updateMethod = this::trainWithMaxViolation;
        }
    }

    @Override
    void trainEach(Sentence sentence) {
        updateMethod.accept(sentence);
        classifier.incrementCount();
    }

    private void trainWithMaxViolation(Sentence sentence) {
        BeamItem[] beam = {new BeamItem(new State(sentence), 0.0)};
        HashMap<State, Double> scoreHistory = new HashMap<>();

        // do beam-search storing score
        boolean terminate = false;
        while (!terminate) {
            beam = getNextBeamItems(beam, beamWidth, classifier, completionService);
            boolean allTerminal = true;
            for (BeamItem item : beam) {
                scoreHistory.putIfAbsent(item.getState(), item.getScore());
                allTerminal = allTerminal && item.getState().isTerminal();
            }
            terminate = allTerminal;
        }

        // find violation
        State.StateIterator predStateIterator = beam[0].getState().getIterator();
        State.StateIterator oracleStateIterator = oracle.getState(sentence).getIterator();
        State predState = predStateIterator.next(); // initial state
        State oracleState = oracleStateIterator.next(); // initial state

        double oracleScore = 0.0;
        double maxViolation = Double.NEGATIVE_INFINITY;
        State maxViolateState = null;
        State pairedOracleState = null;
        while (predStateIterator.hasNext() && oracleStateIterator.hasNext()) {
            predState = predStateIterator.next();
            oracleState = oracleStateIterator.next();

            oracleScore += classifier.getScore(oracleState.prevState.features, oracleState.prevAction);
            double violation = scoreHistory.get(predState) - oracleScore;
            if (violation > maxViolation) {
                maxViolation = violation;
                maxViolateState = predState;
                pairedOracleState = oracleState;
            }
        }

        if (maxViolateState == null) {
            throw new NullPointerException("maxViolateState is null.");
        } else if (!maxViolateState.equals(oracleState)) {
            classifier.update(pairedOracleState, maxViolateState);
        }
    }

    private void trainWithEarlyUpdate(Sentence sentence) {
        State.StateIterator iterator = oracle.getState(sentence).getIterator();
        State oracleState = iterator.next(); // initial state
        BeamItem[] beam = {new BeamItem(new State(sentence), 0.0)};

        boolean terminate = false;
        while (!terminate) {
            oracleState = iterator.next();
            beam = getNextBeamItems(beam, beamWidth, classifier, completionService);
            terminate = Arrays.stream(beam).allMatch(item -> item.getState().isTerminal());

            final State finalOracleState = oracleState; // make a variable final to use it in lambda
            boolean oracleInBeam = Arrays.stream(beam).anyMatch(item -> item.getState().equals(finalOracleState));;
            if (!oracleInBeam || (!terminate && !iterator.hasNext())) {
                classifier.update(oracleState, beam[0].getState()); // early update
                break;
            }
        }
    }

    @Override
    public BeamItem[] getNextBeamItems(BeamItem[] beam, int beamWidth, Perceptron classifier, CompletionService<List<BeamItem>> completionService) {
        try {
            return BeamSearchDecoder.super.getNextBeamItems(beam, beamWidth, classifier, completionService);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
