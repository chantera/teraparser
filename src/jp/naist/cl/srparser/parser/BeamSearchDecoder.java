package jp.naist.cl.srparser.parser;

import jp.naist.cl.srparser.transition.Action;
import jp.naist.cl.srparser.transition.State;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.stream.Collectors;

/**
 * jp.naist.cl.srparser.parser
 *
 * @author Hiroki Teranishi
 */
interface BeamSearchDecoder {

    /**
     * beam-search with single thread
     */
    default List<BeamItem> getNextBeamItems(List<BeamItem> beam, int beamWidth, Perceptron classifier) {
        SortedSet<BeamItem> newbeam = new TreeSet<>();
        int i = -1;
        for (BeamItem item : beam) {
            i++;
            State state = item.getState();
            if (state.isTerminal()) {
                newbeam.add(item);
                continue;
            }
            for (Action action : state.getPossibleActions()) {
                double score = item.getScore() + classifier.getScore(state.getFeatures(), action);
                int priority = 2000 - i * 10 + action.index; // assign higher priority to the preceding item
                newbeam.add(new BeamItem(action.apply(state), score, priority));
            }
        }
        return newbeam.stream().limit(beamWidth).collect(Collectors.toList());
    }

    /**
     * beam-search with multi-thread
     */
    default List<BeamItem> getNextBeamItems(List<BeamItem> beam, int beamWidth, Perceptron classifier, CompletionService<List<BeamItem>> completionService) throws Exception {
        SortedSet<BeamItem> newbeam = new TreeSet<>();
        int i = -1;
        for (BeamItem item : beam) {
            completionService.submit(new BeamTask(item, classifier, ++i));
        }
        for (int j = -1; j < i; j++) {
            newbeam.addAll(completionService.take().get());
        }
        return newbeam.stream().limit(beamWidth).collect(Collectors.toList());
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
            for (Action action : state.getPossibleActions()) {
                double score = item.getScore() + classifier.getScore(state.getFeatures(), action);
                int priority = 2000 - index * 10 + action.index; // assign higher priority to the preceding item
                items.add(new BeamItem(action.apply(state), score, priority));
            }
            return items;
        }
    }

    class BeamItem implements Comparable<BeamItem> {
        private State state;
        private double score;
        private int priority;

        BeamItem(State state, double score) {
            this(state, score, 0);
        }

        BeamItem(State state, double score, int priority) {
            state.setScore(score);
            this.state = state;
            this.score = score;
            this.priority = priority;
        }

        State getState() {
            return state;
        }

        double getScore() {
            return score;
        }

        @Override
        public int compareTo(BeamItem another) {
            double diff = another.score - this.score;
            if (diff > 0) {
                return 1;
            } else if (diff < 0) {
                return -1;
            }
            return another.priority - this.priority;
        }
    }
}
