package jp.naist.cl.srparser.parser;

import jp.naist.cl.srparser.io.Logger;
import jp.naist.cl.srparser.model.Feature;
import jp.naist.cl.srparser.model.Sentence;
import jp.naist.cl.srparser.model.Token;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

/**
 * jp.naist.cl.srparser.parser
 *
 * @author Hiroki Teranishi
 */
public class Trainer extends Parser {
    private Sentence[] sentences;
    private Map<Sentence.ID, Set<Arc>> goldArcSets = new LinkedHashMap<>();
    private Map<Sentence.ID, State> goldState = new LinkedHashMap<>();

    public Trainer(Sentence[] sentences) {
        super(new int[Action.SIZE][Feature.SIZE], new Perceptron());
        this.sentences = sentences;
        int length = sentences.length;
        int i = 0;
        for (Sentence sentence : sentences) {
            Logger.info("Extracting gold data: %d / %d", ++i, length);
            goldArcSets.put(sentence.id, parseGold(sentence));
            goldState.put(sentence.id, state);
        }
        setWeights(new int[Action.SIZE][Feature.SIZE]);
    }

    public void train() {
        train(null);
    }

    public void train(TrainCallback callback) {
        Map<Sentence.ID, Set<Arc>> predArcSets = new LinkedHashMap<>();
        for (Sentence sentence : sentences) {
            predArcSets.put(sentence.id, parse(sentence));
            setWeights(Perceptron.update(weights, goldState.get(sentence.id), state));
        }
        if (callback != null) {
            callback.accept(goldArcSets, predArcSets);
        }
    }

    public void test(TrainCallback callback) {
        Map<Sentence.ID, Set<Arc>> predArcSets = new LinkedHashMap<>();
        for (Sentence sentence : sentences) {
            predArcSets.put(sentence.id, parse(sentence));
        }
        if (callback != null) {
            callback.accept(goldArcSets, predArcSets);
        }
    }

    private Set<Arc> parseGold(Sentence sentence) {
        reset(sentence.tokens);
        while (!state.isTerminal()) {
            state.goldAction.apply(stack, buffer, arcSet);
            state = State.from(state, state.goldAction).createNext(stack, buffer, arcSet);
        }
        return arcSet;
    }

    public interface TrainCallback extends BiConsumer<Map<Sentence.ID, Set<Arc>>, Map<Sentence.ID, Set<Arc>>> {}
}
