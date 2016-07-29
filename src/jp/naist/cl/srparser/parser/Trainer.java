package jp.naist.cl.srparser.parser;

import jp.naist.cl.srparser.io.Logger;
import jp.naist.cl.srparser.model.Sentence;
import jp.naist.cl.srparser.model.Token;
import jp.naist.cl.srparser.transition.Arc;
import jp.naist.cl.srparser.transition.Oracle;

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

    private Map<Sentence.ID, Arc[]> goldArcMap;

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
        goldArcMap = new HashMap<>(length * 4/3);
        int i = 0;
        for (Sentence sentence : sentences) {
            if (++i % REPORT_PERIOD == 0) {
                Logger.info("Extracting Gold data: %d / %d", i, length);
            }
            goldArcMap.put(sentence.id, extractGoldArcs(sentence));
        }
    }

    public int getTrainingSize() {
        return sentences.length;
    }

    public void train() {
        Logger.info("trainer start training");
        int i = 0;
        for (Sentence sentence : sentences) {
            if (++i % REPORT_PERIOD == 0) {
                Logger.info("training: %d / %d ...", i, sentences.length);
            }
            trainEach(sentence);
        }
        Logger.info("trainer finished training");
    }

    public void train(Callback callback) {
        train();
        test(callback);
    }

    abstract void trainEach(Sentence sentence);

    public float[][] getWeights() {
        return classifier.getAveragedWeights();
    }

    public void setWeights(float[][] weights) {
        classifier.setWeights(weights);
    }

    public void test(Callback callback) {
        Map<Sentence.ID, Arc[]> predArcMap = new HashMap<>(sentences.length * 4/3);
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

    public interface Callback extends BiConsumer<Map<Sentence.ID, Arc[]>, Map<Sentence.ID, Arc[]>> {}
}
