package jp.naist.cl.srparser.app;

import jp.naist.cl.srparser.model.Sentence;
import jp.naist.cl.srparser.parser.Arc;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * jp.naist.cl.srparser.app
 *
 * @author Hiroki Teranishi
 */
public class Evaluator {

    public static double calcUAS(Map<Sentence.ID, Arc[]> goldArcSets, Map<Sentence.ID, Arc[]> predictArcSets) {
        double count = 0;
        double collect = 0;
        for (Map.Entry<Sentence.ID, Arc[]> entry : goldArcSets.entrySet()) {
            Arc[] goldSet = entry.getValue();
            Arc[] predictSet = predictArcSets.get(entry.getKey());
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(entry.getKey());
            // i = 0 is null since <ROOT> has no arc.
            for (int i = 1; i < goldSet.length; i++) {
                stringBuilder.append("\ngold=").append(goldSet[i]);
                stringBuilder.append("\npredict=").append(predictSet[i]);
                count++;
                if (goldSet[i].equals(predictSet[i])) {
                    collect++;
                }
                System.out.println(stringBuilder.toString());
            }
            // count += goldSet.length - 1;
            // goldSet.retainAll(predictSet);
            // collect += goldSet.size();
        }
        return collect / count;
    }
}
