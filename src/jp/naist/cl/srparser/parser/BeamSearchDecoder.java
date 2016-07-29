package jp.naist.cl.srparser.parser;

import jp.naist.cl.srparser.transition.Action;
import jp.naist.cl.srparser.transition.State;
import jp.naist.cl.srparser.util.Tuple;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;

/**
 * jp.naist.cl.srparser.parser
 *
 * @author Hiroki Teranishi
 */
interface BeamSearchDecoder {

    /**
     * beam-search with single thread
     */
    default BeamItem[] getNextBeamItems(BeamItem[] beam, int beamWidth, Perceptron classifier) {
        ArrayList<BeamItem> newbeam = new ArrayList<>();
        for (int i = 0; i < beam.length; i++) {
            BeamItem item = beam[i];
            State state = item.getState();
            if (state.isTerminal()) {
                newbeam.add(item);
                continue;
            }
            for (Action action : state.possibleActions) {
                double score = item.getScore() + classifier.getScore(state.features, action);
                int priority = 2000 - i * 10 + action.index; // assign higher priority to the preceding item
                newbeam.add(new BeamItem(action.apply(state), score, priority));
            }
        }
        return newbeam.stream().sorted().limit(beamWidth).toArray(BeamItem[]::new);
    }

    /**
     * beam-search with multi-thread
     */
    default BeamItem[] getNextBeamItems(BeamItem[] beam, int beamWidth, Perceptron classifier, CompletionService<List<BeamItem>> completionService) throws Exception {
        ArrayList<BeamItem> newbeam = new ArrayList<>();
        for (int i = 0; i < beam.length; i++) {
            completionService.submit(new BeamTask(beam[i], classifier, i));
        }
        for (int i = 0; i < beam.length; i++) {
            newbeam.addAll(completionService.take().get());
        }
        return newbeam.stream().sorted().limit(beamWidth).toArray(BeamItem[]::new);
    }

    class BeamTask implements Callable<List<BeamItem>> {
        private BeamItem item;
        private Perceptron classifier;
        private int index;

        BeamTask(BeamItem item, Perceptron classifier, int index) {
            this.item = item;
            this.classifier = classifier;
            this.index = index;
        }

        @Override
        public List<BeamItem> call() {
            List<BeamItem> items = new ArrayList<>();
            State state = item.getState();
            if (state.isTerminal()) {
                items.add(item);
                return items;
            }
            for (Action action : state.possibleActions) {
                double score = item.getScore() + classifier.getScore(state.features, action);
                int priority = 2000 - index * 10 + action.index; // assign higher priority to the preceding item
                items.add(new BeamItem(action.apply(state), score, priority));
            }
            return items;
        }
    }

    class BeamItem extends Tuple<State, Double> implements Comparable<BeamItem> {
        private int priority;

        BeamItem(State state, Double score) {
            this(state, score, 0);
        }

        BeamItem(State state, Double score, int priority) {
            super(state, score);
            this.priority = priority;
        }

        State getState() {
            return this.left;
        }

        double getScore() {
            return this.right;
        }

        @Override
        public int compareTo(BeamItem another) {
            int diff = another.right.compareTo(this.right);
            if (diff != 0) {
                return diff;
            }
            diff = another.priority - this.priority;
            if (diff != 0) {
                return diff;
            }
            return diff;
        }
    }
}
