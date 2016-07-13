package jp.naist.cl.srparser.app;

import jp.naist.cl.srparser.model.Sentence;
import jp.naist.cl.srparser.parser.Arc;
import jp.naist.cl.srparser.parser.Parser;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * jp.naist.cl.srparser.app
 *
 * @author Hiroki Teranishi
 */
public class Evaluator {

    public static double calcUAS(Map<Sentence.ID, Set<Arc>> goldArcSets, Map<Sentence.ID, Set<Arc>> predictArcSets) {
        double count = 0;
        double collect = 0;
        for (Map.Entry<Sentence.ID, Set<Arc>> entry : goldArcSets.entrySet()) {
            Set<Arc> goldSet = new LinkedHashSet<>(entry.getValue());
            Set<Arc> predictSet = predictArcSets.get(entry.getKey());
            count += goldSet.size();
            goldSet.retainAll(predictSet);
            collect += goldSet.size();
        }
        return collect / count;
    }
}
