package jp.naist.cl.srparser.app;

import jp.naist.cl.srparser.model.Sentence;
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

    public static double calcUAS(Map<Sentence.ID, Set<Parser.Arc>> goldArcSets, Map<Sentence.ID, Set<Parser.Arc>> predictArcSets) {
        double count = 0;
        double collect = 0;
        for (Map.Entry<Sentence.ID, Set<Parser.Arc>> entry : goldArcSets.entrySet()) {
            Set<Parser.Arc> goldSet = new LinkedHashSet<>(entry.getValue());
            Set<Parser.Arc> predictSet = predictArcSets.get(entry.getKey());
            count += goldSet.size();
            goldSet.retainAll(predictSet);
            collect += goldSet.size();
        }
        return collect / count;
    }
}
