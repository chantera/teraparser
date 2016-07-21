package jp.naist.cl.srparser.parser;

import jp.naist.cl.srparser.model.Sentence;
import jp.naist.cl.srparser.transition.Oracle;
import jp.naist.cl.srparser.transition.State;

import java.util.Arrays;

/**
 * jp.naist.cl.srparser.parser
 *
 * @author Hiroki Teranishi
 */
public class StructuredLearningTrainer extends Trainer implements BeamSearchDecoder {
    private final int beamWidth;

    public StructuredLearningTrainer(Sentence[] sentences, Oracle oracle, int beamWidth) {
        super(new BeamSearchParser(new Perceptron(), beamWidth), oracle, sentences);
        this.beamWidth = beamWidth;
    }

    @Override
    void trainEach(Sentence sentence) {
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
        classifier.incrementCount();
    }
}
