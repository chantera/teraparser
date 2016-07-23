package jp.naist.cl.srparser.parser;

import jp.naist.cl.srparser.model.Sentence;
import jp.naist.cl.srparser.transition.Oracle;
import jp.naist.cl.srparser.transition.State;

import java.util.Arrays;
import java.util.function.Consumer;

/**
 * jp.naist.cl.srparser.parser
 *
 * @author Hiroki Teranishi
 */
public class StructuredLearningTrainer extends Trainer implements BeamSearchDecoder {
    private final int beamWidth;
    private boolean earlyUpdate;
    private Consumer<Sentence> updateMethod;

    public StructuredLearningTrainer(Sentence[] sentences, Oracle oracle, int beamWidth) {
        this(sentences, oracle, beamWidth, false); // default: earlyUpdate = false
    }

    public StructuredLearningTrainer(Sentence[] sentences, Oracle oracle, int beamWidth, boolean earlyUpdate) {
        super(new BeamSearchParser(new Perceptron(), beamWidth), oracle, sentences);
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
        State.StateIterator iterator = oracle.getState(sentence).getIterator();
        State oracleState = iterator.next(); // initial state
        BeamItem[] beam = {new BeamItem(new State(sentence), 0.0)};

        double maxViolation = Double.NEGATIVE_INFINITY;
        State maxViolateState = null;
        boolean terminate = false;
        while (!terminate) {
            oracleState = iterator.next();
            beam = getNextBeamItems(beam, beamWidth, classifier);

            double oracleScore = classifier.getScore(oracleState.prevState.features, oracleState.prevAction);
            double violation = beam[0].getScore() - oracleScore;
            if (violation > maxViolation) {
                maxViolation = violation;
                maxViolateState = beam[0].getState();
            }
            terminate = !iterator.hasNext() || Arrays.stream(beam).allMatch(item -> item.getState().isTerminal());
        }
        if (maxViolateState == null) {
            throw new NullPointerException("maxViolateState is null.");
        } else if (!maxViolateState.equals(oracleState)) {
            classifier.update(oracleState, maxViolateState);
        }
    }

    private void trainWithEarlyUpdate(Sentence sentence) {
        State.StateIterator iterator = oracle.getState(sentence).getIterator();
        State oracleState = iterator.next(); // initial state
        BeamItem[] beam = {new BeamItem(new State(sentence), 0.0)};

        boolean terminate = false;
        while (!terminate) {
            oracleState = iterator.next();
            beam = getNextBeamItems(beam, beamWidth, classifier);
            terminate = Arrays.stream(beam).allMatch(item -> item.getState().isTerminal());

            final State finalOracleState = oracleState; // make a variable final to use it in lambda
            boolean oracleInBeam = Arrays.stream(beam).anyMatch(item -> item.getState().equals(finalOracleState));;
            if (!oracleInBeam || (!terminate && !iterator.hasNext())) {
                classifier.update(oracleState, beam[0].getState()); // early update
                break;
            }
        }
    }
}
