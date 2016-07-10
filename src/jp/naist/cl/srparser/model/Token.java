package jp.naist.cl.srparser.model;

import java.util.EnumMap;
import java.util.Map;

/**
 * jp.naist.cl.srparser.model
 *
 * @author Hiroki Teranishi
 */
public class Token implements Cloneable {
    public final int id;
    public final String form;
    public final String lemma;
    public final String cpostag;
    public final String postag;
    public final String feats;
    public final int head;
    public final String deprel;
    public final String phead;
    public final String pdeprel;

    public Token(String attributes[]) {
        this.id = Integer.parseInt(attributes[0]);
        this.form = attributes[1];
        this.lemma = attributes[2];
        this.cpostag = attributes[3];
        this.postag = attributes[4];
        this.feats = attributes[5];
        this.head = Integer.parseInt(attributes[6]);
        this.deprel = attributes[7];
        this.phead = attributes[8];
        this.pdeprel = attributes[9];
    }

    private Token(int head, Token token) {
        this.head = head;
        token = token.clone();
        this.id = token.id;
        this.form = token.form;
        this.lemma = token.lemma;
        this.cpostag = token.cpostag;
        this.postag = token.postag;
        this.feats = token.feats;
        this.deprel = token.deprel;
        this.phead = token.phead;
        this.pdeprel = token.pdeprel;
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

    public static Token createNull() {
        String[] attributes = {
            "-10",    // ID
            "<NULL>", // FORM
            "<NULL>", // LEMMA
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
        return form;
    }
}
