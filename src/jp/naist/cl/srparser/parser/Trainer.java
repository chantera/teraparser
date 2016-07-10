package jp.naist.cl.srparser.parser;

import jp.naist.cl.srparser.io.Logger;
import jp.naist.cl.srparser.model.DepTree;
import jp.naist.cl.srparser.model.Feature;
import jp.naist.cl.srparser.model.Sentence;
import jp.naist.cl.srparser.util.Tuple;

import java.util.*;

/**
 * jp.naist.cl.srparser.parser
 *
 * @author Hiroki Teranishi
 */
public class Trainer extends Parser {
    private Parser parser;
    private Sentence[] sentences;
    Map<Sentence.ID, List<Tuple<State, Action>>> goldTransitions = new LinkedHashMap<>();
    Map<Sentence.ID, Set<Tuple<Integer, Integer>>> goldArcSets = new LinkedHashMap<>();

    public Trainer(Sentence[] sentences) {
        setSentences(sentences);
    }

    public void setSentences(Sentence[] sentences) {
        parser = new Parser();
        parser.setClassifier(new Perceptron());
        this.sentences = sentences;
    }

    public Training getIterator(int iteration) {
        return new Training(iteration);
    }

    public class Training implements Iterator<Training> {
        private final int iteration;
        private int current = 1;

        public Training(int iteration) {
            this.iteration = iteration;
        }

        public int getCurrentIteration() {
            return current;
        }

        public void exec() {
            for (Sentence sentence : sentences) {
                List<Tuple<State, Action>> golds = goldTransitions.get(sentence.id);
                Set<Tuple<Integer, Integer>> goldArcSet = goldArcSets.get(sentence.id);
                if (golds == null) {
                    golds = getGoldTransition(sentence);
                    goldTransitions.put(sentence.id, golds);
                    goldArcSet = getArc();
                    goldArcSets.put(sentence.id, goldArcSet);
                }
                Logger.info(sentence);

                Sentence output = parser.parse(sentence);
                List<Tuple<State, Action>> predicts = parser.getTransiaions();
                Set<Tuple<Integer, Integer>> predictArcSet = parser.getArc();

                // Logger.trace(new DepTree(sentence));
                // Logger.trace(new DepTree(output));
                Logger.trace(goldArcSet);
                Logger.trace(predictArcSet);

                Map<Action, Map<Feature.Index, Double>> weights = parser.getWeights();
                weights = Perceptron.update(weights, golds, predicts);
                parser.setWeights(weights);
            }
        }

        @Override
        public boolean hasNext() {
            return current < iteration;
        }

        @Override
        public Training next() {
            if (hasNext()) {
                current++;
                return this;
            }
            return null;
        }

        @Override
        public void remove() {}
    }

    private List<Tuple<State, Action>> getGoldTransition(Sentence sentence) {
        reset(sentence.tokens);
        State state = new State(stack, buffer);
        while (buffer.size() > 0) {
            Action action = getGoldAction(state);
            action.exec(this);
            transitions.add(new Tuple<>(state, action));
            state = new State(stack, buffer);
        }
        transitions.add(new Tuple<State, Action>(state, null));
        return transitions;
    }
}