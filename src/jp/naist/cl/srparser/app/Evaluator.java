package jp.naist.cl.srparser.app;

import jp.naist.cl.srparser.model.Sentence;
import jp.naist.cl.srparser.transition.Arc;

import java.util.Map;

/**
 * jp.naist.cl.srparser.app
 *
 * @author Hiroki Teranishi
 */
public class Evaluator {

    public static double calcUAS(Map<Sentence.ID, Arc[]> goldArcSets, Map<Sentence.ID, Arc[]> predictArcSets) {
        double total = 0;
        double collect = 0;
        for (Map.Entry<Sentence.ID, Arc[]> entry : goldArcSets.entrySet()) {
            Arc[] goldSet = entry.getValue();
            Arc[] predictSet = predictArcSets.get(entry.getKey());
            // i = 0 is null since <ROOT> has no arc.
            for (int i = 1; i < goldSet.length; i++) {
                if (goldSet[i].equals(predictSet[i])) {
                    collect++;
                }
                total++;
            }
        }
        return collect / total;
    }
}
