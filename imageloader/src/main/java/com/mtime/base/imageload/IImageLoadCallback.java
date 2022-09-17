package com.mtime.base.imageload;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;


import java.io.File;

/**
 * Created by ZhouSuQiang on 2017/11/6.
 */

public interface IImageLoadCallback {
    void onLoadCompleted(Bitmap resource);
    void onLoadFailed();
    void onDownloadCompleted(String url, File file);
    void onDownLoadFailed(String url);
    void onLoadCompleted(Drawable resource);
}
