package jp.naist.cl.srparser.parser;

import jp.naist.cl.srparser.model.Sentence;
import jp.naist.cl.srparser.transition.State;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;

/**
 * jp.naist.cl.srparser.parser
 *
 * @author Hiroki Teranishi
 */
public class BeamSearchParser extends GreedyParser implements BeamSearchDecoder {
    private CompletionService<List<BeamItem>> completionService;
    private final int beamWidth;

    public BeamSearchParser(Perceptron classifier, ExecutorService executor, int beamWidth) {
        super(classifier);
        completionService = new ExecutorCompletionService<>(executor);
        this.beamWidth = beamWidth;
    }

    @Override
    public State parse(Sentence sentence) {
        if (beamWidth == 1) {
            return super.parse(sentence); // same as greedy search
        }
        BeamItem[] beam = {new BeamItem(new State(sentence), 0.0)};

        try {
            boolean terminate = false;
            while (!terminate) {
                beam = getNextBeamItems(beam, beamWidth, classifier, completionService);
                terminate = Arrays.stream(beam).allMatch(item -> item.getState().isTerminal());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return beam[0].getState();
    }
}
