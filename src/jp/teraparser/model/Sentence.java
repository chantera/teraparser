package jp.teraparser.model;

import jp.teraparser.util.IntValueObject;
import jp.teraparser.util.StringUtils;

/**
 * jp.teraparser.model
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

    public final class ID extends IntValueObject {
        private ID(int value) {
            super(value);
        }
    }

    @Override
    public String toString() {
        return StringUtils.join(tokens, ' ');
    }
}
