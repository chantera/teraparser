package jp.naist.cl.srparser.model;

import jp.naist.cl.srparser.util.AbstractIntVO;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * jp.naist.cl.srparser.model
 *
 * @author Hiroki Teranishi
 */
public class Feature {
    private static Feature instance = null;
    // private Map<Sentence.ID, Index[]> registry = new LinkedHashMap<>();
    private Map<String, Index> indexes = new HashMap<>();

    private Feature() {}

    public static Feature getInstance() {
        if (instance == null) {
            instance = new Feature();
        }
        return instance;
    }

    public static final class Index extends AbstractIntVO {
        public Index(int value) {
            super(value);
        }
    }

    /*
    public static Index[] getIndexes(Sentence.ID sentenceId) {
        return getInstance().registry.get(sentenceId);
    }

    public static Map<Index, Double> get(Sentence.ID sentenceId) {
        Index[] indexes = getInstance().registry.get(sentenceId);
        if (indexes == null || indexes.length == 0) {
            return null;
        }
        Map<Index, Double> values = new LinkedHashMap<>();
        for (Index index : indexes) {
            values.put(index, values.get(index));
        }
        return values;
    }
    */

    public static Index[] extract(final List<Token> stack, final List<Token> buffer) {
        Map<String, Index> thisIndexes = getInstance().indexes;
        String[] features = FeatureTemplate.generateAll(stack, buffer);
        Index[] indexes = new Index[features.length];

        for (int i = 0; i < features.length; i++) {
            Index index = thisIndexes.get(features[i]);
            if (index == null) {
                thisIndexes.put(features[i], new Index(thisIndexes.size()));
                index = thisIndexes.get(features[i]);
            }
            indexes[i] = index;
        }
        return indexes;
    }

    public static int getSize() {
        return getInstance().indexes.size();
    }
}
