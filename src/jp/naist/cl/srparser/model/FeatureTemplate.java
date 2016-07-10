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
            return this.applyFormat(stack[0].postag);
        }
    },
    S1_POS("s1_pos=%s") {
        @Override
        protected String generate(Token[] stack, Token[] buffer) {
            return this.applyFormat(stack[1].postag);
        }
    },
    B0_POS("b0_pos=%s") {
        @Override
        protected String generate(Token[] stack, Token[] buffer) {
            return this.applyFormat(buffer[0].postag);
        }
    },
    B1_POS("b1_pos=%s") {
        @Override
        protected String generate(Token[] stack, Token[] buffer) {
            return this.applyFormat(buffer[1].postag);
        }
    },
    B2_POS("b2_pos=%s") {
        @Override
        protected String generate(Token[] stack, Token[] buffer) {
            return this.applyFormat(buffer[2].postag);
        }
    },
    B3_POS("b3_pos=%s") {
        @Override
        protected String generate(Token[] stack, Token[] buffer) {
            return this.applyFormat(buffer[3].postag);
        }
    },
    S0_FORM("s0_form=%s") {
        @Override
        protected String generate(Token[] stack, Token[] buffer) {
            return this.applyFormat(stack[0].form);
        }
    },
    B0_FORM("b0_form=%s") {
        @Override
        protected String generate(Token[] stack, Token[] buffer) {
            return this.applyFormat(buffer[0].form);
        }
    },
    B1_FORM("b1_form=%s") {
        @Override
        protected String generate(Token[] stack, Token[] buffer) {
            return this.applyFormat(buffer[1].form);
        }
    },
    // Bigrams
    S0_POS_B0_POS("s0_pos=%s:b0_pos=%s") {
        @Override
        protected String generate(Token[] stack, Token[] buffer) {
            return this.applyFormat(stack[0].postag, buffer[0].postag);
        }
    },
    // Trigrams
    S1_POS_S0_POS_B0_POS("s1_pos=%s:s0_pos=%s:b0_pos=%s") {
        @Override
        protected String generate(Token[] stack, Token[] buffer) {
            return this.applyFormat(stack[1].postag, stack[0].postag, buffer[0].postag);
        }
    },
    S0_POS_B0_POS_B1_POS("s0_pos=%s:b0_pos=%s:b1_pos=%s") {
        @Override
        protected String generate(Token[] stack, Token[] buffer) {
            return this.applyFormat(stack[0].postag, buffer[0].postag, buffer[1].postag);
        }
    },
    B0_POS_B1_POS_B2_POS("b0_pos=%s:b1_pos=%s:b2_pos=%s") {
        @Override
        protected String generate(Token[] stack, Token[] buffer) {
            return this.applyFormat(buffer[0].postag, buffer[1].postag, buffer[2].postag);
        }
    },
    B1_POS_B2_POS_B3_POS("b1_pos=%s:b2_pos=%s:b3_pos=%s") {
        @Override
        protected String generate(Token[] stack, Token[] buffer) {
            return this.applyFormat(buffer[1].postag, buffer[2].postag, buffer[3].postag);
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
