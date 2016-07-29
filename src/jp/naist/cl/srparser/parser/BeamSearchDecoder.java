package jp.naist.cl.srparser.parser;

import jp.naist.cl.srparser.transition.Action;
import jp.naist.cl.srparser.transition.State;
import jp.naist.cl.srparser.util.Tuple;

import java.util.ArrayList;

/**
 * jp.naist.cl.srparser.parser
 *
 * @author Hiroki Teranishi
 */
interface BeamSearchDecoder {

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
