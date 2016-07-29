package jp.naist.cl.srparser.model;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import jp.naist.cl.srparser.util.Tuple;

/**
 * jp.naist.cl.srparser.model
 *
 * @author Hiroki Teranishi
 */
public class Token implements Cloneable {
    private static BiMap<Integer, Tuple<Attribute, String>> attributeRegistry = HashBiMap.create();

    public final int id;
    public final int form;
    // public final int lemma;
    // public final int cpostag;
    public final int postag;
    // public final int feats;
    public final int head;
    public final int deprel;
    // public final int phead;
    // public final int pdeprel;

    public static void setAttributeMap(BiMap<Integer, Tuple<Attribute, String>> attributeMap) {
        attributeRegistry = attributeMap;
        clearCache();
    }

    public static BiMap<Integer, Tuple<Attribute, String>> getAttributeMap() {
        return attributeRegistry;
    }

    private static int registerAttribute(Attribute name, String value) {
        Tuple<Attribute, String> attribute = new Tuple<>(name, value);
        int index = attributeRegistry.inverse().getOrDefault(attribute, -1);
        if (index == -1) {
            synchronized (Token.class) {
                index = attributeRegistry.size();
                attributeRegistry.put(index, attribute);
            }
        }
        return index;
    }

    public static String getAttribute(int index) {
        Tuple<Attribute, String> attribute = attributeRegistry.get(index);
        if (attribute == null) {
            return null;
        }
        return attribute.getRight();
    }

    public enum Attribute {
        ID,
        FORM,
        // LEMMA,
        // CPOSTAG,
        POSTAG,
        // FEATS,
        HEAD,
        DEPREL;
        // PHEAD,
        // PDEPREL;
    }

    public Token(String... attributes) {
        this.id      = Integer.parseInt(attributes[0]);
        this.form    = registerAttribute(Attribute.FORM,    attributes[1]);
        // this.lemma   = registerAttribute(Attribute.LEMMA,   attributes[2]);
        // this.cpostag = registerAttribute(Attribute.CPOSTAG, attributes[3]);
        this.postag  = registerAttribute(Attribute.POSTAG,  attributes[4]);
        // this.feats   = registerAttribute(Attribute.FEATS,   attributes[5]);
        this.head    = Integer.parseInt(attributes[6]);
        this.deprel  = registerAttribute(Attribute.DEPREL,  attributes[7]);
        // this.phead   = registerAttribute(Attribute.PHEAD,   attributes[8]);
        // this.pdeprel = registerAttribute(Attribute.PDEPEL,  attributes[9]);
    }

    private Token(int head, Token token) {
        this.head    = head;
        token        = token.clone();
        this.id      = token.id;
        this.form    = token.form;
        // this.lemma   = token.lemma;
        // this.cpostag = token.cpostag;
        this.postag  = token.postag;
        // this.feats   = token.feats;
        this.deprel  = token.deprel;
        // this.phead   = token.phead;
        // this.pdeprel = token.pdeprel;
    }

    @Override
    public Token clone() {
        Token token;
        try {
            token = (Token) super.clone();
        } catch (CloneNotSupportedException e){
            token = null;
        }
        return token;
    }

    public Token clone(int head) {
        return new Token(head, this);
    }

    private static String preprocessForm(String form) {
        return form.toLowerCase().replaceAll("\\d", "0");
    }

    public static Token clone(Token token, int head) {
        return token.clone(head);
    }

    public static Token createRoot() {
        String[] attributes = {
            "0",      // ID
            "<ROOT>", // FORM
            "<ROOT>", // LEMMA
            "ROOT",   // CPOSTAG
            "ROOT",   // POSTAG
            "_",      // FEATS
            "-1",     // HEAD
            "ROOT",   // DEPREL
            "_",      // PHEAD
            "_"       // PDEPREL
        };
        return new Token(attributes);
    }

    public boolean isRoot() {
        return this.id == 0;
    }

    private static Token pad;

    public static Token createPad() {
        if (pad == null) {
            pad = new Token(
                "-10",    // ID
                "<PAD>",  // FORM
                "<PAD>",  // LEMMA
                "NULL",   // CPOSTAG
                "NULL",   // POSTAG
                "",       // FEATS
                "-11",    // HEAD
                "NULL",   // DEPREL
                "",       // PHEAD
                ""        // PDEPREL
            );
        }
        return pad;
    }

    public boolean isPad() {
        return this.id == -10;
    }

    private static void clearCache() {
        pad = null;
    }

    private volatile int hashCode;

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof Token) {
            final Token other = (Token) obj;
            return this.id == other.id &&
                    this.form == other.form &&
                    this.postag == other.postag &&
                    this.head == other.head &&
                    this.deprel == other.deprel;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int h = hashCode;
        if (h == 0) {
            h = 17;
            h = 31 * h + id;
            h = 31 * h + form;
            h = 31 * h + postag;
            h = 31 * h + head;
            h = 31 * h + deprel;
            hashCode = h;
        }
        return h;
    }

    @Override
    public String toString() {
        return getAttribute(form);
    }
}
