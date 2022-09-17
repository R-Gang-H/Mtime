package com.mtime.base.imageload;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;


/**
 * Created by ZhouSuQiang on 2017/11/6.
 */

public abstract class ImageDownloadCallback implements IImageLoadCallback {
    @Override
    public void onLoadCompleted(Bitmap drawable) {
    }
    
    @Override
    public void onLoadFailed() {
    }
    
    @Override
    public void onLoadCompleted(Drawable resource) {
    }
}
