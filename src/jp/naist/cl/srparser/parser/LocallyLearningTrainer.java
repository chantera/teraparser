package jp.naist.cl.srparser.parser;

import jp.naist.cl.srparser.model.Sentence;
import jp.naist.cl.srparser.transition.Action;
import jp.naist.cl.srparser.transition.Oracle;
import jp.naist.cl.srparser.transition.State;

/**
 * jp.naist.cl.srparser.parser
 *
 * @author Hiroki Teranishi
 */
public class LocallyLearningTrainer extends Trainer {

    public LocallyLearningTrainer(Sentence[] sentences, Oracle oracle) {
        super(new GreedyParser(new Perceptron()), oracle, sentences);
    }

    @Override
    void trainEach(Sentence sentence) {
        State.StateIterator iterator = oracle.getState(sentence).getIterator();
        State oracle = iterator.next(); // initial state
        while (iterator.hasNext()) {
            Action predictAction = classifier.getNextAction(oracle);
            int[] predictFeatures = oracle.features;
            oracle = iterator.next();
            Action oracleAction = oracle.prevAction;
            if (!predictAction.equals(oracleAction)) {
                classifier.update(oracleAction, predictAction, predictFeatures);
            }
            classifier.incrementCount();
        }
    }
}
