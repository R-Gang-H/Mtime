package com.mtime.base.cache;

import android.content.Context;

import com.baijiahulian.common.cache.disk.DiskCacheV2;
import com.google.gson.reflect.TypeToken;
import com.mtime.base.utils.MJsonUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by LiJiaZhi on 17/1/20.
 * <p>
 * 缓存管理类
 */
public class MCacheManager {

    private DiskCacheV2 mDiskCache;

    public MCacheManager() {
    }

    public static MCacheManager DEFAULT = new MCacheManager();

    public void initialize(Context context) {
        //初始化缓存路径
        MFileCache.initialize(context);
    }

    public void setCacheDir(String dir) {
        //todo UserAccountImpl中 调用的两次 会占用百分之75的启动时间，在主线程做读写操作 以后若优化可考虑替换掉这个东西
        try {
            if (mDiskCache != null) mDiskCache.close();

            File file = new File(dir);
            file.mkdirs();
            mDiskCache = DiskCacheV2.create(file, 1024, 50 * 1024 * 1024);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 查找数据
     *
     * @param key 用户使用的key
     * @return 数据或null
     */
    public InputStream getInputStream(String key) {
        if (mDiskCache == null) return null;
        return mDiskCache.getInputStream(key);
    }

    /**
     * 查找数据
     *
     * @param key 用户使用的key
     * @return 数据或null
     */
    public String getString(String key) {
        if (mDiskCache == null) return null;
        return mDiskCache.getString(key);
    }

    /**
     * 是否包含该数据
     *
     * @param key 用户使用的key
     * @return 是否包含该数据
     */
    public boolean contains(String key) {
        return mDiskCache != null && mDiskCache.contains(key);
    }

    /**
     * 向cache中添加数据
     *
     * @param key 用户的key
     * @param is  cache的数据
     * @return 是否成功
     */
    public boolean put(String key, InputStream is) {
        return mDiskCache != null && mDiskCache.put(key, is);
    }

    /**
     * 向cache中添加数据，并设定超时时间
     *
     * @param key             用户的key
     * @param is              cache的数据
     * @param timeoutAtMillis 超时时间 毫秒
     * @return 是否成功
     */
    public boolean put(String key, InputStream is, long timeoutAtMillis) {
        return mDiskCache != null && mDiskCache.put(key, is, timeoutAtMillis);
    }

    /**
     * 向cache中添加数据
     *
     * @param key   用户的key
     * @param value cache的数据
     * @return 是否成功
     */
    public boolean put(String key, String value) {
        return mDiskCache != null && mDiskCache.put(key, value);
    }

    /**
     * 向cache中添加数据
     *
     * @param key             用户的key
     * @param value           cache的数据
     * @param timeoutAtMillis 超时时间 毫秒
     * @return 是否成功
     */
    public boolean put(String key, String value, long timeoutAtMillis) {
        return mDiskCache != null && mDiskCache.put(key, value, timeoutAtMillis);
    }

    /**
     * 删除数据
     *
     * @param key 用户的key
     * @return 是否成功
     */
    public boolean remove(String key) {
        return mDiskCache != null && mDiskCache.delete(key);
    }

    public <T> boolean putModel(String key, T model) {
        return put(key, MJsonUtils.toString(model));
    }

    public <T> boolean putModel(String key, T model, long timeoutAtMillis) {
        return put(key, MJsonUtils.toString(model), timeoutAtMillis);
    }

    public <T> boolean putModelList(String key, List<T> modelList) {
        return put(key, MJsonUtils.toString(modelList));
    }

    public <T> boolean putModelList(String key, List<T> modelList, long timeoutAtMillis) {
        return put(key, MJsonUtils.toString(modelList), timeoutAtMillis);
    }

    public <T> T getModel(String key, Class<T> clazz) {
        String value = getString(key);
        if (value == null) return null;
        return MJsonUtils.parseString(value, clazz);
    }

    public <T> List<T> getModelList(String key, TypeToken<List<T>> typeToken) {
        String value = getString(key);
        if (value == null) return new ArrayList<>();

        return MJsonUtils.parseString(value, typeToken.getType());
    }
}
