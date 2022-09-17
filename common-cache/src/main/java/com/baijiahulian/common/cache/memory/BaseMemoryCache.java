package com.baijiahulian.common.cache.memory;

import java.lang.ref.Reference;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Base memory cache. Implements common functionality for memory cache
 */
public abstract class BaseMemoryCache<K, V> {

    /** Stores not strong references to objects */
    private final Map<K, Reference<V>> softMap = Collections.synchronizedMap(new HashMap<K, Reference<V>>());

    public V get(K key) {
        V result = null;
        Reference<V> reference = softMap.get(key);
        if (reference != null) {
            result = reference.get();
        }
        return result;
    }

    public boolean put(K key, V value) {
        softMap.put(key, createReference(value));
        return true;
    }

    public V remove(K key) {
        Reference<V> bmpRef = softMap.remove(key);
        return bmpRef == null ? null : bmpRef.get();
    }

    public Collection<K> keys() {
        synchronized (softMap) {
            return new HashSet<K>(softMap.keySet());
        }
    }

    public void clear() {
        softMap.clear();
    }

    /** Creates {@linkplain java.lang.ref.Reference not strong} reference of value */
    protected abstract Reference<V> createReference(V value);
}
