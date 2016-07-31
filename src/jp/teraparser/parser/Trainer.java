package jp.teraparser.parser;

import jp.teraparser.io.Logger;
import jp.teraparser.model.Sentence;
import jp.teraparser.model.Token;
import jp.teraparser.transition.Arc;
import jp.teraparser.transition.Oracle;
import jp.teraparser.util.ProgressBar;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * jp.teraparser.parser
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
        test(callback, false);
    }

    abstract void trainEach(Sentence sentence);

    public float[][] getWeights() {
        return classifier.getAveragedWeights();
    }

    public void setWeights(float[][] weights) {
        classifier.setWeights(weights);
    }

    public void test(Callback callback, boolean showProgress) {
        Map<Sentence.ID, Arc[]> predArcMap = new HashMap<>(sentences.length * 4/3);
        if (showProgress) {
            int length = sentences.length;
            int i = 0;
            ProgressBar progressBar = new ProgressBar(System.out);
            System.out.println("TESTING ...");
            for (Sentence sentence : sentences) {
                predArcMap.put(sentence.id, parser.parse(sentence).arcs);
                progressBar.setProgress(++i, length);
            }
            System.out.println("DONE.");
        } else {
            for (Sentence sentence : sentences) {
                predArcMap.put(sentence.id, parser.parse(sentence).arcs);
            }
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
