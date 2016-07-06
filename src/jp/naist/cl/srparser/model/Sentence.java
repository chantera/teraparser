package jp.naist.cl.srparser.model;

import jp.naist.cl.srparser.util.StringUtils;

/**
 * jp.naist.cl.srparser.model
 *
 * @author Hiroki Teranishi
 */
public class Sentence {
    public final Token[] tokens;
    public final int length;

    public Sentence(Token[] tokens) {
        this.tokens = tokens;
        this.length = tokens.length;
    }

    @Override
    public String toString() {
        return StringUtils.join(tokens, ' ');
    }
}
