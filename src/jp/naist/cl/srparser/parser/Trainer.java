package jp.naist.cl.srparser.parser;

import jp.naist.cl.srparser.io.Logger;
import jp.naist.cl.srparser.model.Feature;
import jp.naist.cl.srparser.model.Sentence;
import jp.naist.cl.srparser.model.Token;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * jp.naist.cl.srparser.parser
 *
 * @author Hiroki Teranishi
 */
public class Trainer extends Parser {
    private static final int REPORT_PERIOD = 100;

    private Sentence[] sentences;
    private Oracle oracle;
    private HashMap<Sentence.ID, Arc[]> goldArcSets = new HashMap<>();

    public Trainer(Sentence[] sentences, Oracle oracle) {
        super(new int[Action.SIZE][Feature.SIZE], new Perceptron());
        loadGolds(sentences, oracle);
        setWeights(new int[Action.SIZE][Feature.SIZE]);
    }

    private void loadGolds(Sentence[] sentences, Oracle oracle) {
        int length = sentences.length;
        int i = 0;
        for (Sentence sentence : sentences) {
            if (++i % REPORT_PERIOD == 0) {
                Logger.info("Extracting gold data: %d / %d", i, length);
            }
            // oracle.getState(sentence); // making cache
            goldArcSets.put(sentence.id, parseGold(sentence));
        }
        this.sentences = sentences;
        this.oracle = oracle;
    }

    public void train() {
        train(null);
    }

    public void train(TrainCallback callback) {
        Map<Sentence.ID, Arc[]> predArcSets = new LinkedHashMap<>();
        int i = 0;
        for (Sentence sentence : sentences) {
            if (++i % REPORT_PERIOD == 0) {
                Logger.info("training: %d of %d ...", i, sentences.length);
            }
            //Action[] oracleActions = oracle.getActions(sentence);
            //List<Action> predictActions = new ArrayList<>();
            State state = new State(sentence);
            while (!state.isTerminal()) {
                Action action = getNextAction(state);
                state = action.apply(state);
                //predictActions.add(action);
            }
            Arc[] goldArcs = goldArcSets.get(sentence.id);
            Arc[] predictArcs = state.arcs;
            for (i = 0; i < predictArcs.length; i++) {
                if (predictArcs[i] != goldArcs[i]) {
                    setWeights(Perceptron.update(weights, oracle.getState(sentence), state));
                    break;
                }
            }
            predArcSets.put(sentence.id, state.arcs);
        }
        if (callback != null) {
            callback.accept(goldArcSets, predArcSets);
        }
    }

    public void test(TrainCallback callback) {
        Map<Sentence.ID, Arc[]> predArcSets = new LinkedHashMap<>();
        for (Sentence sentence : sentences) {
            predArcSets.put(sentence.id, parse(sentence));
        }
        if (callback != null) {
            callback.accept(goldArcSets, predArcSets);
        }
    }

    private Arc[] parseGold(Sentence sentence) {
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
