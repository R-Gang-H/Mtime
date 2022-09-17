package com.mtime.statistic.large;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mtime on 2017/10/28.
 */

public class MapBuild<K, V> {

    public MapBuild(Map<K, V> map) {
        if (null != map && !map.isEmpty()) {
            mBuildTarget = new HashMap<>(map);
        } else {
            mBuildTarget = new HashMap<>();
        }
    }

    public MapBuild() {
        mBuildTarget = new HashMap<>();
    }

    private final Map<K, V> mBuildTarget;

    public MapBuild<K, V> put(K k, V v) {
        mBuildTarget.put(k, v);
        return this;
    }

    public MapBuild<K, V> clear() {
        mBuildTarget.clear();
        return this;
    }

    public Map<K, V> build() {
        return mBuildTarget;
    }

}
