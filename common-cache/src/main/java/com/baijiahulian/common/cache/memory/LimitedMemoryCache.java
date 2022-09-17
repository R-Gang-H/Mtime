package com.baijiahulian.common.cache.memory;

import android.util.Log;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Limited cache. Provides object storing. Size of all stored bitmaps will not to exceed size limit
 */
public abstract class LimitedMemoryCache<K, V> extends BaseMemoryCache<K, V> {

    private static final String TAG = "LimitedMemoryCache";

    private static final int MAX_NORMAL_CACHE_SIZE_IN_MB = 16;
    private static final int MAX_NORMAL_CACHE_SIZE = MAX_NORMAL_CACHE_SIZE_IN_MB * 1024 * 1024;

    private final int sizeLimit;

    private final AtomicInteger cacheSize;

    /**
     * Contains strong references to stored objects. Each next object is added last. If hard cache size will exceed
     * limit then first object is deleted (but it continue exist at {@link #softMap} and can be collected by GC at any
     * time)
     */
    private final List<V> hardCache = Collections.synchronizedList(new LinkedList<V>());

    /** @param sizeLimit Maximum size for cache (in bytes) */
    public LimitedMemoryCache(int sizeLimit) {
        this.sizeLimit = sizeLimit;
        cacheSize = new AtomicInteger();
        if (sizeLimit > MAX_NORMAL_CACHE_SIZE) {
            Log.w(TAG, "You set too large memory cache size (more than " + MAX_NORMAL_CACHE_SIZE_IN_MB + " Mb)");
        }
    }

    @Override
    public boolean put(K key, V value) {
        boolean putSuccessfully = false;
        // Try to add value to hard cache
        int valueSize = getSize(value);
        int sizeLimit = getSizeLimit();
        int curCacheSize = cacheSize.get();
        if (valueSize < sizeLimit) {
            while (curCacheSize + valueSize > sizeLimit) {
                V removedValue = removeNext();
                if (hardCache.remove(removedValue)) {
                    curCacheSize = cacheSize.addAndGet(-getSize(removedValue));
                }
            }
            hardCache.add(value);
            cacheSize.addAndGet(valueSize);

            putSuccessfully = true;
        }
        // Add value to soft cache
        super.put(key, value);
        return putSuccessfully;
    }

    @Override
    public V remove(K key) {
        V value = super.get(key);
        if (value != null) {
            if (hardCache.remove(value)) {
                cacheSize.addAndGet(-getSize(value));
            }
        }
        return super.remove(key);
    }

    @Override
    public void clear() {
        hardCache.clear();
        cacheSize.set(0);
        super.clear();
    }

    protected int getSizeLimit() {
        return sizeLimit;
    }

    protected abstract int getSize(V value);

    protected abstract V removeNext();
}
