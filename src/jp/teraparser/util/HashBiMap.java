package jp.teraparser.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * jp.teraparser.util
 *
 * @author Hiroki Teranishi
 */
public class HashBiMap<K,V> extends BiMap<K,V> implements Serializable {
    private static final long serialVersionUID = 2562182290881000492L;

    static final int DEFAULT_INITIAL_CAPACITY = 1 << 4;
    static final float DEFAULT_LOAD_FACTOR = 0.75f;

    public HashBiMap() {
        super(new HashMap<K, V>(), new HashMap<V, K>());
    }

    public HashBiMap(int initialCapacity) {
        this(initialCapacity, DEFAULT_LOAD_FACTOR);
    }

    public HashBiMap(int initialCapacity, float loadFactor) {
         super(new HashMap<K, V>(initialCapacity, loadFactor), new HashMap<V, K>(initialCapacity, loadFactor));
    }

    @Override
    public Map<V,K> inverse() {
        return new HashMap<>(vkMap);
    }

    private void writeObject(final ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeObject(kvMap);
    }

    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        kvMap = new HashMap<>();
        vkMap = new HashMap<>();
        @SuppressWarnings("unchecked") // will fail at runtime if stream is incorrect
        final Map<K, V> map = (Map<K, V>) in.readObject();
        putAll(map);
    }
}
