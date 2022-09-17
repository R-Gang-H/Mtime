package com.baijiahulian.common.cache.memory;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

/**
 * Memory cache with java.lang.ref.WeakReference weak references
 */
public class WeakMemoryCache<K, V> extends BaseMemoryCache<K, V> {
    @Override
    protected Reference<V> createReference(V value) {
        return new WeakReference<V>(value);
    }
}
