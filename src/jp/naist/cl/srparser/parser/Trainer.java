package jp.naist.cl.srparser.parser;

import jp.naist.cl.srparser.io.Logger;
import jp.naist.cl.srparser.model.Feature;
import jp.naist.cl.srparser.model.Sentence;
import jp.naist.cl.srparser.model.Token;
import jp.naist.cl.srparser.transition.Action;
import jp.naist.cl.srparser.transition.Arc;
import jp.naist.cl.srparser.transition.Oracle;
import jp.naist.cl.srparser.transition.State;

import java.util.*;
import java.util.function.BiConsumer;

/**
 * jp.naist.cl.srparser.parser
 *
 * @author Hiroki Teranishi
 */
public class Trainer extends BeamSearchParser {
    private static final int REPORT_PERIOD = 100;

    private Sentence[] sentences;
    private Oracle oracle;
    private HashMap<Sentence.ID, Arc[]> goldArcMap = new HashMap<>();

    public Trainer(Sentence[] sentences, Oracle oracle) {
        super(new Perceptron(new float[Action.SIZE][Feature.SIZE]), 4);
        loadGolds(sentences, oracle);
    }

    private void loadGolds(Sentence[] sentences, Oracle oracle) {
        int length = sentences.length;
        int i = 0;
        for (Sentence sentence : sentences) {
            if (++i % REPORT_PERIOD == 0) {
                Logger.info("Extracting gold data: %d / %d", i, length);
            }
            goldArcMap.put(sentence.id, extractGoldArcs(sentence));
        }
        this.sentences = sentences;
        this.oracle = oracle;
    }

    public void train() {
        train(null);
    }

    public void train(TrainCallback callback) {
        Map<Sentence.ID, Arc[]> predArcMap = new LinkedHashMap<>();
        int i = 0;
        for (Sentence sentence : sentences) {
            if (++i % REPORT_PERIOD == 0) {
                Logger.info("training: %d of %d ...", i, sentences.length);
            }
            predArcMap.put(sentence.id, trainEach(sentence).arcs);
        }
        if (callback != null) {
            callback.accept(goldArcMap, predArcMap);
        }
    }

    private State trainEach(Sentence sentence) {
        /*
        State state = parse(sentence);
        Arc[] goldArcs = goldArcMap.get(sentence.id);
        Arc[] predictArcs = state.arcs;
        for (int i = 1; i < goldArcs.length; i++) { // i = 0 is a root-dependent arc (none)
            if (!goldArcs[i].equals(predictArcs[i])) {
                classifier.update(oracle.getState(sentence), state);
                break;
            }
        }
        */
        int beamWidth = 4;
        /*
        if (beamWidth == 1) {
            return super.parse(sentence);
        }
        */

        State.StateIterator iterator = oracle.getState(sentence).getIterator();
        State oracleState = iterator.next();
        BeamItem[] beam = {new BeamItem(new State(sentence), 0.0)};

        boolean terminate = false;
        while (!terminate) {
            if (!iterator.hasNext()) {
                classifier.update(oracleState, beam[0].getState());
                break;
            }
            oracleState = iterator.next();
            Action oracleAction = oracleState.prevAction;
            ArrayList<BeamItem> newbeam = new ArrayList<>();
            for (BeamItem item : beam) {
                State state = item.getState();
                if (state.isTerminal()) {
                    newbeam.add(item);
                    continue;
                }
                for (Action action : state.possibleActions) {
                    double score = item.getScore() + classifier.getScore(state.features, action);
                    newbeam.add(new BeamItem(action.apply(state), score));
                }
            }
            beam = newbeam.stream().sorted().limit(beamWidth).toArray(BeamItem[]::new);
            boolean oracleInBeam = Arrays.stream(beam).anyMatch(item -> item.getState().prevAction.equals(oracleAction));;
            if (!oracleInBeam) {
                // System.err.println("early update.");
                classifier.update(oracleState, beam[0].getState());
                // classifier.update(oracle.getState(sentence), beam[0].getState());
                break;
            }
            terminate = Arrays.stream(beam).allMatch(item -> item.getState().isTerminal());
        }
        // return beam[0].getState();
        classifier.incrementCount();
        return parse(sentence);
        /*
        State.StateIterator iterator = oracle.getState(sentence).getIterator();
        State oracle = iterator.next(); // initial state
        while (iterator.hasNext()) {
            Action predictAction = classifier.getNextAction(oracle);
            int[] predictFeatures = oracle.features;
            oracle = iterator.next();
            Action oracleAction = oracle.prevAction;
            if (!predictAction.equals(oracleAction)) {
                classifier.update(oracleAction, predictAction, predictFeatures);
            }
            classifier.incrementCount();
        }
        return parse(sentence);
        */
    }

    public float[][] getWeights() {
        return classifier.getAveragedWeights();
    }

    public void setWeights(float[][] weights) {
        classifier.setWeights(weights);
    }

    public void test(TrainCallback callback) {
        Map<Sentence.ID, Arc[]> predArcMap = new LinkedHashMap<>();
        for (Sentence sentence : sentences) {
            predArcMap.put(sentence.id, parse(sentence).arcs);
        }
        if (callback != null) {
            callback.accept(goldArcMap, predArcMap);
        }
    }

    private Arc[] extractGoldArcs(Sentence sentence) {
        Arc[] goldArcs = new Arc[sentence.tokens.length];
        int i = 0;
        for (Token token : sentence.tokens) {
            if (!token.isRoot()) {
                goldArcs[++i] = new Arc(token.head, token.id);
            }
        }
        return goldArcs;
    }

    public interface TrainCallback extends BiConsumer<Map<Sentence.ID, Arc[]>, Map<Sentence.ID, Arc[]>> {}
}
