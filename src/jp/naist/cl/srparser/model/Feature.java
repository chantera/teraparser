package jp.naist.cl.srparser.model;

import jp.naist.cl.srparser.parser.State;
import jp.naist.cl.srparser.util.HashUtils;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.stream.IntStream;

/**
 * jp.naist.cl.srparser.model
 *
 * @author Hiroki Teranishi
 */
public class Feature {
    public static final int SIZE = (int) Math.pow(2, 23);

    public static int[] extract(List<Token> stack, List<Token> buffer) {
        int stackSize  = stack.size();
        int bufferSize = buffer.size();
        Token pad = Token.createPad();
        Token s0 = stackSize > 0 ? stack.get(stackSize - 1) : pad;
        Token s1 = stackSize > 1 ? stack.get(stackSize - 2) : pad;
        Token b0 = bufferSize > 0 ? buffer.get(0) : pad;
        Token b1 = bufferSize > 1 ? buffer.get(1) : pad;
        Token b2 = bufferSize > 2 ? buffer.get(2) : pad;
        Token b3 = bufferSize > 3 ? buffer.get(3) : pad;

        int[] features = {
            Template.generate(Template.S0_POS.hash,               s0.postag                      ),
            Template.generate(Template.S1_POS.hash,               s1.postag                      ),
            Template.generate(Template.B0_POS.hash,               b0.postag                      ),
            Template.generate(Template.B1_POS.hash,               b1.postag                      ),
            Template.generate(Template.B2_POS.hash,               b2.postag                      ),
            Template.generate(Template.B3_POS.hash,               b3.postag                      ),
            Template.generate(Template.S0_FORM.hash,              s0.form                        ),
            Template.generate(Template.B0_FORM.hash,              b0.form                        ),
            Template.generate(Template.B1_FORM.hash,              b1.form                        ),
            Template.generate(Template.S0_POS_B0_POS.hash,        s0.postag, b0.postag           ),
            Template.generate(Template.S1_POS_S0_POS_B0_POS.hash, s1.postag, s0.postag, b0.postag),
            Template.generate(Template.S0_POS_B0_POS_B1_POS.hash, s0.postag, b0.postag, b1.postag),
            Template.generate(Template.B0_POS_B1_POS_B2_POS.hash, b0.postag, b1.postag, b2.postag),
            Template.generate(Template.B1_POS_B2_POS_B3_POS.hash, b1.postag, b2.postag, b3.postag)
        };
        return features;
    }

    public static int[] extract(State state) {
        Token pad = Token.createPad();
        Token s0 = state.getStackTokenOrDefault(0, pad);
        Token s1 = state.getStackTokenOrDefault(1, pad);
        Token b0 = state.getBufferTokenOrDefault(0, pad);
        Token b1 = state.getBufferTokenOrDefault(1, pad);
        Token b2 = state.getBufferTokenOrDefault(2, pad);
        Token b3 = state.getBufferTokenOrDefault(3, pad);

        int[] features = {
                Template.generate(Template.S0_POS.hash,               s0.postag                      ),
                Template.generate(Template.S1_POS.hash,               s1.postag                      ),
                Template.generate(Template.B0_POS.hash,               b0.postag                      ),
                Template.generate(Template.B1_POS.hash,               b1.postag                      ),
                Template.generate(Template.B2_POS.hash,               b2.postag                      ),
                Template.generate(Template.B3_POS.hash,               b3.postag                      ),
                Template.generate(Template.S0_FORM.hash,              s0.form                        ),
                Template.generate(Template.B0_FORM.hash,              b0.form                        ),
                Template.generate(Template.B1_FORM.hash,              b1.form                        ),
                Template.generate(Template.S0_POS_B0_POS.hash,        s0.postag, b0.postag           ),
                Template.generate(Template.S1_POS_S0_POS_B0_POS.hash, s1.postag, s0.postag, b0.postag),
                Template.generate(Template.S0_POS_B0_POS_B1_POS.hash, s0.postag, b0.postag, b1.postag),
                Template.generate(Template.B0_POS_B1_POS_B2_POS.hash, b0.postag, b1.postag, b2.postag),
                Template.generate(Template.B1_POS_B2_POS_B3_POS.hash, b1.postag, b2.postag, b3.postag)
        };
        return features;
    }

    private enum Template {
        // Unigrams
        S0_POS("s0p"),
        S1_POS("s1p"),
        B0_POS("b0p"),
        B1_POS("b1p"),
        B2_POS("b2p"),
        B3_POS("b3p"),
        S0_FORM("s0f"),
        B0_FORM("b0f"),
        B1_FORM("b1f"),
        // Bigrams
        S0_POS_B0_POS("s0p:b0p"),
        // Trigrams
        S1_POS_S0_POS_B0_POS("s1p:s0p:b0p"),
        S0_POS_B0_POS_B1_POS("s0p:b0p:b1p"),
        B0_POS_B1_POS_B2_POS("b0p:b1p:b2p"),
        B1_POS_B2_POS_B3_POS("b1p:b2p:b3p");

        private final String label;
        private final int hash;

        private Template(String label) {
            this.label = label;
            this.hash = label.hashCode();
        }

        private int create(int... attributes) {
            int[] key = IntStream.concat(IntStream.of(hash), Arrays.stream(attributes)).toArray();
            return generate(key);
        }

        private static int generate(int... key) {
            int hash = HashUtils.oneAtATimeHash(key);
            return Math.abs(hash) % SIZE;
        }
    }
}
