package jp.naist.cl.srparser.model;

import jp.naist.cl.srparser.transition.State;
import jp.naist.cl.srparser.util.HashUtils;

/**
 * jp.naist.cl.srparser.model
 *
 * @author Hiroki Teranishi
 */
public class Feature {
    public static final int SIZE = (int) Math.pow(2, 23);

    public static int[] extract(State state) {
        Token pad = Token.createPad();
        Token s0 = state.getStackTokenOrDefault(0, pad);
        Token s0h;
        Token s0l;
        Token s0r;
        if (!s0.isPad()) {
            s0h = !s0.isRoot() ? state.getToken(s0.head) : pad;
            s0l = state.getLeftmostTokenOrDefault(s0.id, pad);
            s0r = state.getRightmostTokenOrDefault(s0.id, pad);
        } else {
            s0h = pad;
            s0l = pad;
            s0r = pad;
        }
        Token b0 = state.getBufferTokenOrDefault(0, pad);
        Token b1 = state.getBufferTokenOrDefault(1, pad);
        Token b2 = state.getBufferTokenOrDefault(2, pad);
        Token b0l;
        if (!b0.isPad()) {
            b0l = state.getLeftmostTokenOrDefault(b0.id, pad);
        } else {
            b0l = pad;
        }

        int[] features = {
                Template.generate(Template.S0_FORM_POS.hash,             s0.form,    s0.postag                       ),
                Template.generate(Template.B0_FORM_POS.hash,             b0.form,    b0.postag                       ),
                Template.generate(Template.B1_FORM_POS.hash,             b1.form,    b1.postag                       ),
                Template.generate(Template.B2_FORM_POS.hash,             b2.form,    b2.postag                       ),
                Template.generate(Template.S0_FORM.hash,                 s0.form                                     ),
                Template.generate(Template.B0_FORM.hash,                 b0.form                                     ),
                Template.generate(Template.B1_FORM.hash,                 b1.form                                     ),
                Template.generate(Template.B2_FORM.hash,                 b2.form                                     ),
                Template.generate(Template.S0_POS.hash,                  s0.postag                                   ),
                Template.generate(Template.B0_POS.hash,                  b0.postag                                   ),
                Template.generate(Template.B1_POS.hash,                  b1.postag                                   ),
                Template.generate(Template.B2_POS.hash,                  b2.postag                                   ),
                Template.generate(Template.S0_FORM_POS_B0_FORM_POS.hash, s0.form,    s0.postag,  b0.form,   b0.postag),
                Template.generate(Template.S0_FORM_POS_B0_FORM.hash,     s0.form,    s0.postag,  b0.form             ),
                Template.generate(Template.S0_FORM_B0_FORM_POS.hash,     s0.form,    b0.form,    b0.postag           ),
                Template.generate(Template.S0_FORM_POS_B0_POS.hash,      s0.form,    s0.postag,  b0.postag           ),
                Template.generate(Template.S0_POS_B0_FORM_POS.hash,      s0.postag,  b0.form,    b0.postag           ),
                Template.generate(Template.S0_FORM_B0_FORM.hash,         s0.form,    b0.form                         ),
                Template.generate(Template.S0_POS_B0_POS.hash,           s0.postag,  b0.postag                       ),
                Template.generate(Template.B0_POS_B1_POS.hash,           b0.postag,  b1.postag                       ),
                Template.generate(Template.B0_POS_B1_POS_B2_POS.hash,    b0.postag,  b1.postag,  b2.postag           ),
                Template.generate(Template.S0_POS_B0_POS_B1_POS.hash,    s0.postag,  b0.postag,  b1.postag           ),
                Template.generate(Template.S0H_POS_S0_POS_B0_POS.hash,   s0h.postag, s0.postag,  b0.postag           ),
                Template.generate(Template.S0_POS_S0L_POS_B0_POS.hash,   s0.postag,  s0l.postag, b0.postag           ),
                Template.generate(Template.S0_POS_S0R_POS_B0_POS.hash,   s0.postag,  s0r.postag, b0.postag           ),
                Template.generate(Template.S0_POS_B0_POS_B0L_POS.hash,   s0.postag,  b0.postag,  b0l.postag          ),
        };
        return features;
    }

    private enum Template {
        // Unigrams
        S0_FORM_POS("s0fp"),
        B0_FORM_POS("b0fp"),
        B1_FORM_POS("b1fp"),
        B2_FORM_POS("b2fp"),
        S0_FORM("s0f"),
        B0_FORM("b0f"),
        B1_FORM("b1f"),
        B2_FORM("b2f"),
        S0_POS("s0p"),
        B0_POS("b0p"),
        B1_POS("b1p"),
        B2_POS("b2p"),
        // Bigrams
        S0_FORM_POS_B0_FORM_POS("s0fp:b0fp"),
        S0_FORM_POS_B0_FORM("s0fp:b0f"),
        S0_FORM_B0_FORM_POS("s0f:b0fp"),
        S0_FORM_POS_B0_POS("s0fp:b0p"),
        S0_POS_B0_FORM_POS("s0p:b0fp"),
        S0_FORM_B0_FORM("s0f:b0f"),
        S0_POS_B0_POS("s0p:b0p"),
        B0_POS_B1_POS("b0p:b1p"),
        // Trigrams
        B0_POS_B1_POS_B2_POS("b0p:b1p:b2p"),
        S0_POS_B0_POS_B1_POS("s0p:b0p:b1p"),
        S0H_POS_S0_POS_B0_POS("s0hp:s0p:b0p"),
        S0_POS_S0L_POS_B0_POS("s0p:s0lp:b0p"),
        S0_POS_S0R_POS_B0_POS("s0p:s0rp:b0p"),
        S0_POS_B0_POS_B0L_POS("s0p:b0p:b0lp");

        private final String label;
        private final int hash;

        Template(String label) {
            this.label = label;
            this.hash = label.hashCode();
        }

        private static int generate(int... key) {
            int hash = HashUtils.oneAtATimeHash(key);
            return Math.abs(hash) % SIZE;
        }
    }
}
