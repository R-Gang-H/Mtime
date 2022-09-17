package com.mtime.mtmovie.network;

import android.content.Context;

import com.mtime.mtmovie.network.utils.CheckUtil;

import java.io.File;
import java.io.IOException;

import okhttp3.Cache;

/**
 * Created by yinguanping on 17/1/13.
 * 缓存的内容提供器,提供缓存路径和缓存时间,默认设置10M
 * 如果缓存的数据量大于这个值，内部会使用lur规则进行删除。
 */

public class CacheProvide {
    private final Context mContext;
    private final File cacheFile;
    Cache cache;
    public static final int CACHE_SIZE = 10 * 1024 * 1024;

    public CacheProvide(Context context, String cachePath) {
        mContext = context;
        this.cacheFile = !CheckUtil.checkNULL(cachePath) ? creatFile(cachePath) : mContext.getCacheDir();
        cache = new Cache(cacheFile, CACHE_SIZE);
    }

    public void clearCache() {
        try {
            cache.evictAll();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //使用应用缓存文件路径，缓存大小为10MB
    public Cache provideCache() {
        return cache;
    }

    private File creatFile(String path) {
        return new File(path);
    }
}
