package jp.naist.cl.srparser.parser;

import jp.naist.cl.srparser.model.Sentence;
import jp.naist.cl.srparser.transition.Action;
import jp.naist.cl.srparser.transition.State;
import jp.naist.cl.srparser.util.Tuple;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * jp.naist.cl.srparser.parser
 *
 * @author Hiroki Teranishi
 */
public class BeamSearchParser extends Parser {
    private final int beamWidth;

    public BeamSearchParser(Perceptron classifier, int beamWidth) {
        super(classifier);
        this.beamWidth = beamWidth;
    }

    @Override
    public State parse(Sentence sentence) {
        if (beamWidth == 1) {
            return super.parse(sentence);
        }
        BeamItem[] beam = {new BeamItem(new State(sentence), 0.0)};

        boolean terminate = false;
        while (!terminate) {
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
            beam = newbeam.stream().sorted().limit(beamWidth).toArray(BeamItem[]::new);
            terminate = Arrays.stream(beam).allMatch(item -> item.getState().isTerminal());
        }
        return beam[0].getState();
    }

    protected class BeamItem extends Tuple<State, Double> implements Comparable {
        public BeamItem(State left, Double right) {
            super(left, right);
        }

        public State getState() {
            return this.left;
        }

        public double getScore() {
            return this.right;
        }

        @Override
        public int compareTo(Object o) {
            return ((BeamItem) o).right.compareTo(this.right);
        }
    }
}
