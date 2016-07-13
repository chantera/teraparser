package jp.naist.cl.srparser.parser;

import jp.naist.cl.srparser.util.Tuple;

/**
 * jp.naist.cl.srparser.parser
 *
 * @author Hiroki Teranishi
 */
public class Arc extends Tuple<Integer, Integer> {
    public final int head;
    public final int dependent;

    public Arc(Integer head, Integer dependent) {
        super(head, dependent);
        this.head = super.left;
        this.dependent = super.right;
    }
}
