package jp.teraparser.util;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * jp.teraparser.util
 *
 * @author Hiroki Teranishi
 */
public class BiMap<K,V> implements Map<K,V> {
    final Map<K,V> kvMap;
    final Map<V,K> vkMap;

    BiMap(Map<K,V> kvMap, Map<V,K> vkMap) {
        if (kvMap == null || vkMap == null) {
            throw new NullPointerException();
        }
        this.kvMap = kvMap;
        this.vkMap = vkMap;
    }

    @Override
    public void clear() {
        kvMap.clear();
        vkMap.clear();
    }

    @Override
    public boolean containsKey(Object key) {
        return kvMap.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return vkMap.containsValue(value);
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return kvMap.entrySet();
    }

    @Override
    public V get(Object key) {
        return kvMap.get(key);
    }

    public K getKey(Object value) {
        return vkMap.get(value);
    }

    @Override
    public boolean isEmpty() {
        return kvMap.isEmpty();
    }

    @Override
    public Set<K> keySet() {
        return kvMap.keySet();
    }

    @Override
    public V put(K key, V value) {
        // if (key == null || value == null) {
        //     throw new IllegalArgumentException();
        // }
        K oldKey = vkMap.put(value, key);
        if (oldKey != null) {
            kvMap.remove(oldKey);
        }
        return kvMap.put(key, value);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> map) {
        for (Entry<? extends K, ? extends V> e : map.entrySet()) {
            put(e.getKey(), e.getValue());
        }
    }

    @Override
    public V remove(Object key) {
        V removedValue = kvMap.remove(key);
        if (removedValue != null) {
            vkMap.remove(removedValue);
        }
        return removedValue;
    }

    @Override
    public int size() {
        return kvMap.size();
    }

    @Override
    public Collection<V> values() {
        return vkMap.keySet();
    }

    public Map<V,K> inverse() {
        return new BiMap<>(vkMap, kvMap);
    }
}
