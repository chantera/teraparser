package jp.naist.cl.srparser.parser;

import jp.naist.cl.srparser.model.Sentence;
import jp.naist.cl.srparser.transition.Oracle;
import jp.naist.cl.srparser.transition.State;
import jp.naist.cl.srparser.util.TestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.Assert.*;

/**
 * jp.naist.cl.srparser.parser
 *
 * @author Hiroki Teranishi
 */
public class BeamSearchDecoderTest implements BeamSearchDecoder {
    private static final int N_THREADS = 8;
    private static final int ITERATION = 10;
    private static final int BEAMWIDTH = 16;

    private ExecutorService executor;
    private Oracle oracle;
    private Perceptron classifier;
    private int iteration;
    private int beamWidth;
    private Sentence sentence;

    @Before
    public void setUp() {
        executor = Executors.newFixedThreadPool(N_THREADS);
        oracle = new Oracle(Oracle.Algorithm.STATIC);
        classifier = new Perceptron();
        iteration = ITERATION;
        beamWidth = BEAMWIDTH;
        sentence = TestUtils.getSampleSentence();
        System.out.println("Sample sentence: " + sentence);
    }

    @After
    public void tearDown() throws Exception {
        executor.shutdownNow();
    }

    @Test
    public void multithreadTest() {
        CompletionService<List<BeamItem>> completionService = new ExecutorCompletionService<>(executor);
        for (int i = 0; i < iteration; i ++) {
            trainEachWithEarlyUpdate(sentence, completionService);
        }
    }

    private void trainEachWithEarlyUpdate(Sentence sentence, CompletionService<List<BeamItem>> completionService) {
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

    public BeamSearchDecoder.BeamItem[] getNextBeamItems(BeamSearchDecoder.BeamItem[] beam, int beamWidth, Perceptron classifier, CompletionService<List<BeamItem>> completionService) {
        try {
            BeamSearchDecoder.BeamItem[] item1 = BeamSearchDecoder.super.getNextBeamItems(beam, beamWidth, classifier);
            BeamSearchDecoder.BeamItem[] item2 = BeamSearchDecoder.super.getNextBeamItems(beam, beamWidth, classifier, completionService);
            for (int i = 0; i < item1.length; i++) {
                System.out.println("item1: " + item1[i].getState() + ": score=" + item1[i].getScore());
                System.out.println("item2: " + item2[i].getState() + ": score=" + item2[i].getScore());
                if (!item1[i].equals(item2[i])) {
                    throw new Exception(item1[i] + " != " + item2[i]);
                }
            }
            System.out.println("========");
            return item2;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}