package jp.naist.cl.srparser.model;

import java.util.List;

/**
 * jp.naist.cl.srparser.model
 *
 * @author Hiroki Teranishi
 */
enum FeatureTemplate {
    // Unigrams
    S0_POS("s0_pos=%s") {
        @Override
        protected String generate(Token[] stack, Token[] buffer) {
            return this.applyFormat(stack[0].getAttribute(Token.Attribute.POSTAG));
        }
    },
    S1_POS("s1_pos=%s") {
        @Override
        protected String generate(Token[] stack, Token[] buffer) {
            return this.applyFormat(stack[1].getAttribute(Token.Attribute.POSTAG));
        }
    },
    B0_POS("b0_pos=%s") {
        @Override
        protected String generate(Token[] stack, Token[] buffer) {
            return this.applyFormat(buffer[0].getAttribute(Token.Attribute.POSTAG));
        }
    },
    B1_POS("b1_pos=%s") {
        @Override
        protected String generate(Token[] stack, Token[] buffer) {
            return this.applyFormat(buffer[1].getAttribute(Token.Attribute.POSTAG));
        }
    },
    B2_POS("b2_pos=%s") {
        @Override
        protected String generate(Token[] stack, Token[] buffer) {
            return this.applyFormat(buffer[2].getAttribute(Token.Attribute.POSTAG));
        }
    },
    B3_POS("b3_pos=%s") {
        @Override
        protected String generate(Token[] stack, Token[] buffer) {
            return this.applyFormat(buffer[3].getAttribute(Token.Attribute.POSTAG));
        }
    },
    S0_FORM("s0_form=%s") {
        @Override
        protected String generate(Token[] stack, Token[] buffer) {
            return this.applyFormat(stack[0].getAttribute(Token.Attribute.FORM));
        }
    },
    B0_FORM("b0_form=%s") {
        @Override
        protected String generate(Token[] stack, Token[] buffer) {
            return this.applyFormat(buffer[0].getAttribute(Token.Attribute.FORM));
        }
    },
    B1_FORM("b1_form=%s") {
        @Override
        protected String generate(Token[] stack, Token[] buffer) {
            return this.applyFormat(buffer[1].getAttribute(Token.Attribute.FORM));
        }
    },
    // Bigrams
    S0_POS_B0_POS("s0_pos=%s:b0_pos=%s") {
        @Override
        protected String generate(Token[] stack, Token[] buffer) {
            return this.applyFormat(
                    stack[0].getAttribute(Token.Attribute.CPOSTAG),
                    buffer[0].getAttribute(Token.Attribute.CPOSTAG)
            );
        }
    },
    // Trigrams
    S1_POS_S0_POS_B0_POS("s1_pos=%s:s0_pos=%s:b0_pos=%s") {
        @Override
        protected String generate(Token[] stack, Token[] buffer) {
            return this.applyFormat(
                    stack[1].getAttribute(Token.Attribute.CPOSTAG),
                    stack[0].getAttribute(Token.Attribute.CPOSTAG),
                    buffer[0].getAttribute(Token.Attribute.CPOSTAG)
            );
        }
    },
    S0_POS_B0_POS_B1_POS("s0_pos=%s:b0_pos=%s:b1_pos=%s") {

        @Override
        protected String generate(Token[] stack, Token[] buffer) {
            return this.applyFormat(
                    stack[0].getAttribute(Token.Attribute.CPOSTAG),
                    buffer[0].getAttribute(Token.Attribute.CPOSTAG),
                    buffer[1].getAttribute(Token.Attribute.CPOSTAG)
            );
        }
    },
    B0_POS_B1_POS_B2_POS("b0_pos=%s:b1_pos=%s:b2_pos=%s") {

        @Override
        protected String generate(Token[] stack, Token[] buffer) {
            return this.applyFormat(
                    buffer[0].getAttribute(Token.Attribute.CPOSTAG),
                    buffer[1].getAttribute(Token.Attribute.CPOSTAG),
                    buffer[2].getAttribute(Token.Attribute.CPOSTAG)
            );
        }
    },
    B1_POS_B2_POS_B3_POS("b1_pos=%s:b2_pos=%s:b3_pos=%s") {

        @Override
        protected String generate(Token[] stack, Token[] buffer) {
            return this.applyFormat(
                    buffer[1].getAttribute(Token.Attribute.CPOSTAG),
                    buffer[2].getAttribute(Token.Attribute.CPOSTAG),
                    buffer[3].getAttribute(Token.Attribute.CPOSTAG)
            );
        }
    };

    private static final int WINDOW = 4;

    private String format;

    FeatureTemplate(String format) {
        this.format = format;
    }

    protected String applyFormat(String... values) {
        return String.format(format, values);
    }

    protected abstract String generate(Token[] stack, Token[] buffer);

    public static String[] generateAll(final List<Token> stack, final List<Token> buffer) {
        Token[] s = new Token[WINDOW];
        Token[] b = new Token[WINDOW];
        for (int i = 0; i < WINDOW; i++ ) {
            Token token;
            int index = stack.size() - 1 - i;
            if (index >= 0) {
                token = stack.get(stack.size() - (i + 1));
            } else {
                token = Token.createNull();
            }
            s[i] = token;
        }
        for (int i = 0; i < WINDOW; i++ ) {
            Token token;
            if (i < buffer.size()) {
                token = buffer.get(i);
            } else {
                token = Token.createNull();
            }
            b[i] = token;
        }
        FeatureTemplate[] templates = FeatureTemplate.values();
        String[] features = new String[templates.length];
        for (int i = 0; i < templates.length; i++) {
            features[i] = templates[i].generate(s, b);
        }
        return features;
    }
}
