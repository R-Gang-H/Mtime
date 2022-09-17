package com.mtime.base.imageload;

import android.graphics.drawable.Drawable;

import java.io.File;

/**
 * Created by ZhouSuQiang on 2017/11/6.
 */

public abstract class ImageShowLoadCallback implements IImageLoadCallback {
    @Override
    public void onDownloadCompleted(String url, File file) {
    }
    
    @Override
    public void onDownLoadFailed(String url) {
    }
    
    @Override
    public void onLoadCompleted(Drawable resource) {
    }
}
