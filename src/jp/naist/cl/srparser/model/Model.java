package jp.naist.cl.srparser.model;

import com.google.common.collect.BiMap;
import jp.naist.cl.srparser.util.Tuple;

/**
 * jp.naist.cl.srparser.model
 *
 * @author Hiroki Teranishi
 */
public class Model extends Tuple<BiMap<Integer, Tuple<Token.Attribute, String>>, float[][]> {

    public Model(BiMap<Integer, Tuple<Token.Attribute, String>> attributeMap, float[][] weights) {
        super(attributeMap, weights);
    }

    public BiMap<Integer, Tuple<Token.Attribute, String>> getAttributeMap() {
        return super.getLeft();
    }

    public float[][] getWeight() {
        return super.getRight();
    }
}
