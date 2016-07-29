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
        return newbeam.stream().sorted().limit(beamWidth).toArray(BeamItem[]::new);
    }

    /**
     * beam-search with multi-thread
     */
    default BeamItem[] getNextBeamItems(BeamItem[] beam, int beamWidth, Perceptron classifier, CompletionService<List<BeamItem>> completionService) throws Exception {
        ArrayList<BeamItem> newbeam = new ArrayList<>();
        for (BeamItem item : beam) {
            completionService.submit(new BeamTask(item, classifier));
        }
        for (int i = 0; i < beam.length; i++) {
            newbeam.addAll(completionService.take().get());
        }
        return newbeam.stream().sorted().limit(beamWidth).toArray(BeamItem[]::new);
    }

    class BeamTask implements Callable<List<BeamItem>> {
        private BeamItem item;
        private Perceptron classifier;

        BeamTask(BeamItem item, Perceptron classifier) {
            this.item = item;
            this.classifier = classifier;
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
                items.add(new BeamItem(action.apply(state), score));
            }
            return items;
        }
    }

    class BeamItem extends Tuple<State, Double> implements Comparable {
        BeamItem(State state, Double score) {
            super(state, score);
        }

        State getState() {
            return this.left;
        }

        double getScore() {
            return this.right;
        }

        @Override
        public int compareTo(Object o) {
            return ((BeamItem) o).right.compareTo(this.right);
        }
    }
}
