package jp.teraparser.model;

import jp.teraparser.util.BiMap;
import jp.teraparser.util.Tuple;

/**
 * jp.teraparser.model
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
