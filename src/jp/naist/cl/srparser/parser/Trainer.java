package jp.naist.cl.srparser.parser;

import jp.naist.cl.srparser.io.Logger;
import jp.naist.cl.srparser.model.Sentence;
import jp.naist.cl.srparser.model.Token;
import jp.naist.cl.srparser.transition.Arc;
import jp.naist.cl.srparser.transition.Oracle;
import jp.naist.cl.srparser.transition.State;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * jp.naist.cl.srparser.parser
 *
 * @author Hiroki Teranishi
 */
public abstract class Trainer {
    private static final int REPORT_PERIOD = 100;

    private Sentence[] sentences;
    Perceptron classifier;
    Oracle oracle;
    private Parser parser;

    private HashMap<Sentence.ID, Arc[]> goldArcMap = new HashMap<>();

    public Trainer(Parser parser, Oracle oracle) {
        this.parser = parser;
        this.classifier = parser.getClassifier();
        this.oracle = oracle;
    }

    Trainer(Parser parser, Oracle oracle, Sentence[] sentences) {
        this(parser, oracle);
        setTrainingSentences(sentences);
    }

    public void setTrainingSentences(Sentence[] sentences) {
        this.sentences = sentences;
        loadGolds();
    }

    private void loadGolds() {
        int length = sentences.length;
        int i = 0;
        for (Sentence sentence : sentences) {
            if (++i % REPORT_PERIOD == 0) {
                Logger.info("Extracting gold data: %d / %d", i, length);
            }
            goldArcMap.put(sentence.id, extractGoldArcs(sentence));
        }
    }

    public void train() {
        train(null);
    }

    public void train(TrainCallback callback) {
        HashMap<Sentence.ID, Arc[]> predArcMap = new HashMap<>();
        int i = 0;
        for (Sentence sentence : sentences) {
            if (++i % REPORT_PERIOD == 0) {
                Logger.info("training: %d of %d ...", i, sentences.length);
            }
            trainEach(sentence);
            State predict = parser.parse(sentence);
            predArcMap.put(sentence.id, predict.arcs);
        }
        if (callback != null) {
            callback.accept(goldArcMap, predArcMap);
        }
    }

    abstract void trainEach(Sentence sentence);

    public float[][] getWeights() {
        return classifier.getAveragedWeights();
    }

    public void setWeights(float[][] weights) {
        classifier.setWeights(weights);
    }

    public void test(TrainCallback callback) {
        HashMap<Sentence.ID, Arc[]> predArcMap = new HashMap<>();
        for (Sentence sentence : sentences) {
            predArcMap.put(sentence.id, parser.parse(sentence).arcs);
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
