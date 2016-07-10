package jp.naist.cl.srparser.model;

import jp.naist.cl.srparser.util.AbstractIntVO;
import jp.naist.cl.srparser.util.StringUtils;

/**
 * jp.naist.cl.srparser.model
 *
 * @author Hiroki Teranishi
 */
public class Sentence {
    public final Token[] tokens;
    public final int length;
    public final ID id;

    public Sentence(int id, Token[] tokens) {
        this.id = new ID(id);
        this.tokens = tokens;
        this.length = tokens.length;
    }

    public final class ID extends AbstractIntVO {
        private ID(int value) {
            super(value);
        }
    }

    @Override
    public String toString() {
        return StringUtils.join(tokens, ' ');
    }
}
