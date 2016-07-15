package jp.naist.cl.srparser.model;

import jp.naist.cl.srparser.util.Tuple;

import java.util.LinkedList;
import java.util.List;


/**
 * jp.naist.cl.srparser.model
 *
 * @author Hiroki Teranishi
 */
public class Token implements Cloneable {
    private static List<Tuple<Attribute, String>> attributeRegistry = new LinkedList<>();

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

    private static int registerAttribute(Attribute name, String value) {
        Tuple<Attribute, String> attribute = new Tuple<>(name, value);
        int index = attributeRegistry.indexOf(attribute);
        if (index == -1) {
            synchronized (Token.class) {
                index = attributeRegistry.size();
                attributeRegistry.add(attribute);
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

    public Token(String attributes[]) {
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
        }catch (CloneNotSupportedException e){
            token = null;
        }
        return token;
    }

    public Token clone(int head) {
        return new Token(head, this);
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

    public static Token createPad() {
        String[] attributes = {
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
        };
        return new Token(attributes);
    }

    @Override
    public String toString() {
        return getAttribute(form);
    }
}
