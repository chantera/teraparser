package jp.naist.cl.srparser.model;

import com.google.common.collect.HashBiMap;
import jp.naist.cl.srparser.util.Tuple;

/**
 * jp.naist.cl.srparser.model
 *
 * @author Hiroki Teranishi
 */
public class Model extends Tuple<HashBiMap<Integer, Tuple<Token.Attribute, String>>, float[][]> {

    public Model(HashBiMap<Integer, Tuple<Token.Attribute, String>> attributeMap, float[][] weights) {
        super(attributeMap, weights);
    }

    public HashBiMap<Integer, Tuple<Token.Attribute, String>> getAttributeMap() {
        return super.getLeft();
    }

    public float[][] getWeight() {
        return super.getRight();
    }
}
