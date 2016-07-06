package jp.naist.cl.srparser.model;

import java.util.EnumMap;
import java.util.Map;

/**
 * jp.naist.cl.srparser.model
 *
 * @author Hiroki Teranishi
 */
public class Token {
    public final int id;
    public final int head;
    private final Map<Attribute, String> attributes;

    public Token(String attributes[]) {
        this.id = Integer.parseInt(attributes[0]);
        this.head = Integer.parseInt(attributes[6]);
        this.attributes = Attribute.createAttributes(attributes);
    }

    public Token(Map<Attribute, String> attributes) {
        this.id = Integer.parseInt(attributes.get(Attribute.ID));
        this.head = Integer.parseInt(attributes.get(Attribute.HEAD));
        this.attributes = attributes;
    }

    public Map<Attribute, String> cloneAttributes() {
        return ((EnumMap<Attribute, String>) attributes).clone();
    }

    public String getAttribute(Attribute attributeName) {
        return attributes.get(attributeName);
    }

    public enum Attribute {
        ID,
        FORM,
        LEMMMA,
        CPOSTAG,
        POSTAG,
        FEATS,
        HEAD,
        DEPREL,
        PHEAD,
        PDEPREL;

        public static final Attribute[] values = Attribute.values();
        public static final int size = values.length;

        private static Map<Attribute, String> createAttributes(String attributes[]) {
            if (attributes.length != Attribute.size) {
                throw new IllegalArgumentException("attributes length should be " + Attribute.size);
            }
            Map<Attribute, String> attributeMap = new EnumMap<>(Attribute.class);
            int i = 0;
            for (Attribute name : values) {
                attributeMap.put(name, attributes[i]);
                i++;
            }
            return attributeMap;
        }
    }

    public static Token createRoot() {
        String[] attributes = {
            "0",      // ID
            "<ROOT>", // FORM
            "_",      // LEMMA
            "ROOT",   // CPOSTAG
            "ROOT",   // POSTAG
            "_",      // FEATS
            "1",      // HEAD
            "_",      // DEPREL
            "_",      // PHEAD
            "_"       // PDEPREL
        };
        return new Token(attributes);
    }

    @Override
    public String toString() {
        return getAttribute(Attribute.FORM);
    }
}
