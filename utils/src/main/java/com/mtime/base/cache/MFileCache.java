package com.mtime.base.cache;

import android.content.Context;
import android.os.Environment;

/**
 * Created by LiJiaZhi on 17/3/23.
 * 缓存路径
 */

public class MFileCache {
    /**   默认缓存  包括：网络数据缓存   */
    public static  String CACHE_PATH = Environment.getExternalStorageDirectory().getPath() + "/cinema/cache/";
    /**   不删除的路径   */
    public static  String CACHE_PATH_NO_CLEAN = Environment.getExternalStorageDirectory().getPath() + "/cinema/user_data/";
    /**   临时图片文件   glide网络图片、拍照上传、裁剪上传头像等   */
    public static  String CACHE_TEMP_PIC_PATH = Environment.getExternalStorageDirectory().getPath() + "/cinema/temp_pic/";
    /** 网络缓存目录 */
    public static String NETWORK_CACHE = CACHE_PATH + "network/";

    public static void initialize(Context context) {
        CACHE_PATH = getCachePath(context) + "/cache/";

        NETWORK_CACHE = CACHE_PATH + "network/";

        CACHE_TEMP_PIC_PATH = getCachePath(context)+ "/temp_pic/";

        CACHE_PATH_NO_CLEAN = context.getCacheDir().getPath()+ "/user_data/";//清除缓存的时候不会被清除getCacheDir
    }

    /**
     * 小米商店monkey手机，sd状态有问题，就只能暂用沙盒路径
     * @param context
     * @return
     */
    private static String getCachePath(Context context){
        return null==context.getExternalCacheDir()?context.getCacheDir().getPath():context.getExternalCacheDir().getPath();
    }
}
