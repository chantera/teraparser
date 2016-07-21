package jp.naist.cl.srparser.parser;

import jp.naist.cl.srparser.model.Sentence;
import jp.naist.cl.srparser.transition.State;

import java.util.Arrays;

/**
 * jp.naist.cl.srparser.parser
 *
 * @author Hiroki Teranishi
 */
public class BeamSearchParser extends GreedyParser implements BeamSearchDecoder {
    private final int beamWidth;

    public BeamSearchParser(Perceptron classifier, int beamWidth) {
        super(classifier);
        this.beamWidth = beamWidth;
    }

    @Override
    public State parse(Sentence sentence) {
        if (beamWidth == 1) {
            return super.parse(sentence); // same as greedy search
        }
        BeamItem[] beam = {new BeamItem(new State(sentence), 0.0)};

        boolean terminate = false;
        while (!terminate) {
            beam = getNextBeamItems(beam, beamWidth, classifier);
            terminate = Arrays.stream(beam).allMatch(item -> item.getState().isTerminal());
        }
        return beam[0].getState();
    }
}
