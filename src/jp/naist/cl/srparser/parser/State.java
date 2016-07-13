package jp.naist.cl.srparser.parser;

import jp.naist.cl.srparser.model.Feature;
import jp.naist.cl.srparser.model.Token;

import java.util.LinkedList;
import java.util.Set;

/**
 * jp.naist.cl.srparser.parser
 *
 * @author Hiroki Teranishi
 */
public class State {
    public final LinkedList<Token> stack;
    public final LinkedList<Token> buffer;
    public final Set<Arc> arcSet;
    public final int[] features;

    public State(final LinkedList<Token> stack, final LinkedList<Token> buffer, final Set<Arc> arcSet) {
        this.stack = stack;
        this.buffer = buffer;
        this.arcSet = arcSet;
        this.features = Feature.extract(stack, buffer);
    }
}
