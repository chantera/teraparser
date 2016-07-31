package jp.naist.cl.srparser.model;

import jp.naist.cl.srparser.util.BiMap;
import jp.naist.cl.srparser.util.Tuple;

/**
 * jp.naist.cl.srparser.model
 *
 * @author Hiroki Teranishi
 */
public class Model extends Tuple<BiMap<Tuple<Token.Attribute, String>, Integer>, float[][]> {

    public Model(BiMap<Tuple<Token.Attribute, String>, Integer> attributeMap, float[][] weights) {
        super(attributeMap, weights);
    }

    public BiMap<Tuple<Token.Attribute, String>, Integer> getAttributeMap() {
        return super.getLeft();
    }

    public float[][] getWeight() {
        return super.getRight();
    }
}
