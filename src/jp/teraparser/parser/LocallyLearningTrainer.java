package jp.teraparser.parser;

import jp.teraparser.model.Sentence;
import jp.teraparser.transition.Action;
import jp.teraparser.transition.Oracle;
import jp.teraparser.transition.State;

/**
 * jp.teraparser.parser
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
            int[] predictFeatures = oracle.getFeatures();
            oracle = iterator.next();
            Action oracleAction = oracle.prevAction;
            if (predictAction != oracleAction) {
                classifier.update(oracleAction, predictAction, predictFeatures);
            }
            classifier.incrementCount();
        }
    }
}
