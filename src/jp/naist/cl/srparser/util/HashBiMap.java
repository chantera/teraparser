package jp.naist.cl.srparser.util;

import java.util.HashMap;
import java.util.Map;

/**
 * jp.naist.cl.srparser.util
 *
 * @author Hiroki Teranishi
 */
public class HashBiMap<K,V> extends BiMap<K,V> {
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
}
