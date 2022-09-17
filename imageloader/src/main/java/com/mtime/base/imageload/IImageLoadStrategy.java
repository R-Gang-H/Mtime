package com.mtime.base.imageload;

import android.content.Context;
import androidx.annotation.NonNull;

import java.io.File;
import java.util.Map;


/**
 * Created by Administrator on 2017/3/20 0020.
 */

public interface IImageLoadStrategy {
    void download(@NonNull ImageLoadOptions options);
    
    void loadImage(@NonNull ImageLoadOptions options);
    
    boolean clearDiskCache();
    
    void clearMemory();
    
    void pause(Object context);
    
    void resume(Object context);
    
    // 在application的oncreate中初始化
    void init(Context context, Config config);
    
    long getDiskCacheSize();
    
    void deleteImgFromDiskCache(String url);
    
    File getImgFileFromDiskCache(String url);
    
    class Config {
        public Map<String, String> headers;
        public String cacheDiskPath;

        public Config setCacheDiskPath(String cacheDiskPath) {
            this.cacheDiskPath = cacheDiskPath;
            return this;
        }
    }
}
