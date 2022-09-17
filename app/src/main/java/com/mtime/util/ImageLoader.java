/**
 * Copyright (C) 2013 The Android Open Source Project
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mtime.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.mtime.frame.App;
import com.mtime.R;
import com.mtime.base.imageload.ImageHelper;
import com.mtime.base.imageload.ImageLoadOptions;
import com.mtime.base.imageload.ImageLoadStrategyManager;
import com.mtime.base.imageload.ImageProxyUrl;
import com.mtime.base.imageload.ImageShowLoadCallback;
import com.mtime.constant.FrameConstant;
import com.mtime.widgets.NetworkImageView;

import java.io.File;


/**
 * Created by yinguanping on 17/2/21.
 * 重构原volley ImageLoader类。为了方便项目里面不改动代码
 */
@Deprecated
public class ImageLoader {

    public interface ImageListener {
        void onResponse(ImageContainer response, boolean isImmediate);

        void onErrorResponse(VolleyError volleyError);
    }

    public void clearAll(Context context) {
        ImageLoadStrategyManager.getInstance().clearDiskCache();
        ImageLoadStrategyManager.getInstance().clearMemory();
    }

    /**
     * Container object for all of the data surrounding an image request.
     */
    public class ImageContainer {
        /**
         * The most relevant bitmap for the container. If the image was in cache, the
         * Holder to use for the final bitmap (the one that pairs to the requested URL).
         */
        private Bitmap mBitmap;

        public void setmBitmap(Bitmap mBitmap) {
            this.mBitmap = mBitmap;
        }

        /**
         * Returns the bitmap associated with the request URL if it has been loaded, null otherwise.
         */
        public Bitmap getBitmap() {
            return mBitmap;
        }
    }

    /*class DefaultImageGetConfig extends ImageGetConfig {

        private ImageListener listener;

        protected DefaultImageGetConfig(Builder builder) {
            super(builder);
        }

        public ImageListener getListener() {
            return listener;
        }

        public void setListener(ImageListener listener) {
            this.listener = listener;
        }
    }*/

    /*public void cancleRequest(Context context) {
        ImageGet.cancelAllTasks(context);
    }*/

    public void loadBitmap(Context context, String url, final ImageListener listener) {
        /*ImageGet.loadBitmap(context, url, new BitmapLoadingListener() {
            @Override
            public void onSuccess(Bitmap bitmap) {
                if (null != listener) {
                    ImageContainer imageContainer = new ImageContainer();
                    imageContainer.setmBitmap(bitmap);
                    listener.onResponse(imageContainer, false);
                }
            }

            @Override
            public void onError() {
                if (null != listener) {
                    listener.onErrorResponse(new VolleyError("Failed"));
                }
            }
        });*/
    
        ImageHelper.with(context).load(url).callback(new ImageShowLoadCallback() {
            @Override
            public void onLoadCompleted(Bitmap resource) {
                if(null != listener) {
                    ImageContainer imageContainer = new ImageContainer();
                    imageContainer.setmBitmap(resource);
                    listener.onResponse(imageContainer, false);
                }
            }

            @Override
            public void onLoadCompleted(Drawable resource) {
                if (null != listener && resource instanceof BitmapDrawable) {
                    ImageContainer imageContainer = new ImageContainer();
                    imageContainer.setmBitmap(((BitmapDrawable)resource).getBitmap());
                    listener.onResponse(imageContainer, true);
                }
            }
    
            @Override
            public void onLoadFailed() {
                if (null != listener) {
                    listener.onErrorResponse(new VolleyError("Failed"));
                }
            }
        }).showload();
    }

    public void displayFile(final File file, ImageView imageView, final int defaultId, final int errorId, int width, int height, final ImageListener listener) {
        /*ImageGetConfig.Builder builder = DefaultImageGetConfig.parseBuilder(ImageGet.defConfig)
                .setPlaceHolderResId(defaultId)
                .setErrorResId(errorId)
                .setSize(new ImageGetConfig.OverrideSize(width, height));
        final DefaultImageGetConfig defaultImageGetConfig = new DefaultImageGetConfig(builder);
        defaultImageGetConfig.setListener(listener);
        ImageGet.loadFile(imageView, file, defaultImageGetConfig, new LoaderListener() {
            @Override
            public void onSuccess(Bitmap bitmap, boolean b, boolean b1) {
                if (null != defaultImageGetConfig.getListener()) {
                    ImageContainer imageContainer = new ImageContainer();
                    imageContainer.setmBitmap(bitmap);
                    defaultImageGetConfig.getListener().onResponse(imageContainer, b);
                }
            }

            @Override
            public void onError() {
                if (null != defaultImageGetConfig.getListener()) {
                    defaultImageGetConfig.getListener().onErrorResponse(new VolleyError("Failed"));
                }
            }
        });*/

        ImageShowLoadCallback callback = null == listener ? null : new ImageShowLoadCallback() {
            @Override
            public void onLoadCompleted(Bitmap resource) {
                if (null != listener) {
                    ImageContainer imageContainer = new ImageContainer();
                    imageContainer.setmBitmap(resource);
                    listener.onResponse(imageContainer, true);
                }
            }

            @Override
            public void onLoadCompleted(Drawable resource) {
                if (null != listener && resource instanceof BitmapDrawable) {
                    ImageContainer imageContainer = new ImageContainer();
                    imageContainer.setmBitmap(((BitmapDrawable)resource).getBitmap());
                    listener.onResponse(imageContainer, true);
                }
            }

            @Override
            public void onLoadFailed() {
                if (null != listener) {
                    listener.onErrorResponse(new VolleyError("Failed"));
                }
            }
        };
        
        ImageHelper.with()
                .placeholder(defaultId)
                .error(errorId)
                .view(imageView)
                .load(file)
                .override(width, height)
                .callback(callback)
                .showload();
    }

    public void displayImage(final String uri, ImageListener listener) {
        this.displayImage(uri, null, 0, 0, listener);
    }

    /**
     * @param uri
     * @param imageView
     * @param style
     * @param listener
     */
    public void displayImage(final String uri, final ImageView imageView, final ImageURLManager.ImageStyle style, ImageListener listener) {
        this.displayImage(uri, imageView, ImageURLManager.getWidth(style), ImageURLManager.getHeight(style), listener);
    }

    public void displayImage(final String uri, final ImageView imageView, final int defaultId, final int errorId, final ImageURLManager.ImageStyle style, ImageListener listener) {
        int w = ImageURLManager.getWidth(style);
        int h = ImageURLManager.getHeight(style);
        this.displayImage(uri, imageView, defaultId, errorId, w, h, ImageURLManager.FIX_WIDTH_AND_HEIGHT, listener);
    }

    public void displayImage(final String uri, final ImageView imageView, final int w, final int h, ImageListener listener) {
        this.displayImage(uri, imageView, w, h, ImageURLManager.FIX_WIDTH_AND_HEIGHT, listener);
    }

    public void displayImage(final String uri, final ImageView imageView, final int defaultId, final int errorId, final int w, final int h, ImageListener listener) {
        this.displayImage(uri, imageView, defaultId, errorId, w, h, ImageURLManager.FIX_WIDTH_AND_HEIGHT, listener);
    }

    public void displayImageGif(final String uri, final ImageView imageView, final int defaultId, final int errorId) {
        this._displayImageGif(uri, imageView, defaultId, errorId);
    }

    public void displayImage(final String uri, final ImageView imageView, final int w, final int h, final int clipType, ImageListener listener) {
        this.displayImage(uri, imageView, R.drawable.default_image, R.drawable.default_image, w, h, clipType, listener);
    }

    public void displayImage(final String uri, final ImageView imageView, final int defaultId, final int errorId, final ImageURLManager.ImageStyle style, final int clipType, ImageListener listener) {
        int w = ImageURLManager.getWidth(style);
        int h = ImageURLManager.getHeight(style);
        this.displayImage(uri, imageView, defaultId, errorId, w, h, clipType, listener);
    }

    public void displayImage(final String uri, final ImageView imageView, final int defaultId, final int errorId,
                             final int w, final int h, final int clipType, final ImageListener listener) {
        // TODO image view和 listener不能同时为null.
        if (TextUtils.isEmpty(uri) || (null == imageView && null == listener)) {
            if (null != listener) {
                listener.onErrorResponse(new VolleyError("error paramsters"));
            }
            return;
        }
    
        ImageProxyUrl.ClipType type = ImageProxyUrl.ClipType.FIX_WIDTH_AND_HEIGHT;
        for(ImageProxyUrl.ClipType c : ImageProxyUrl.ClipType.values()) {
            if(c.getValue() == clipType)
                type = c;
        }

        ImageShowLoadCallback callback = null == listener ? null : new ImageShowLoadCallback() {
            @Override
            public void onLoadCompleted(Bitmap resource) {
                if (null != listener) {
                    ImageContainer imageContainer = new ImageContainer();
                    imageContainer.setmBitmap(resource);
                    listener.onResponse(imageContainer, true);
                }
            }

            @Override
            public void onLoadCompleted(Drawable resource) {
                if (null != listener && resource instanceof BitmapDrawable) {
                    ImageContainer imageContainer = new ImageContainer();
                    imageContainer.setmBitmap(((BitmapDrawable)resource).getBitmap());
                    listener.onResponse(imageContainer, true);
                }
            }

            @Override
            public void onLoadFailed() {
                if (null != listener) {
                    listener.onErrorResponse(new VolleyError("Failed"));
                }
            }
        };

        ImageHelper.with(ImageProxyUrl.SizeType.CUSTOM_SIZE, type)
                .override(w, h)
                .placeholder(defaultId)
                .error(errorId)
                .load(uri)
                .view(imageView)
                .callback(callback)
                .showload();
        
    

        /*int width = w;
        int height = h;
        if (width <= 0) {
            width = FrameConstant.SCREEN_WIDTH;
        }
        if (height <= 0) {
            height = FrameConstant.SCREEN_HEIGHT;
        }

        String url = ImageURLManager.getRequestURL(uri, width, height, clipType);

        if (null == imageView && !TextUtils.isEmpty(url) && null != listener) {//这时可能是只加载bitmap
            loadBitmap(App.getInstance().getApplicationContext(), url, listener);
            return;
        }

        ImageGetConfig.Builder builder = DefaultImageGetConfig.parseBuilder(ImageGet.defConfig)
                .setPlaceHolderResId(defaultId)
                .setErrorResId(errorId)
                .setSize(new ImageGetConfig.OverrideSize(width, height));
        final DefaultImageGetConfig defaultImageGetConfig = new DefaultImageGetConfig(builder);
        defaultImageGetConfig.setListener(listener);
        ImageGet.loadImage(imageView, url, defaultImageGetConfig, new LoaderListener() {
            @Override

            public void onSuccess(Bitmap bitmap, boolean b, boolean b1) {
                if (null != defaultImageGetConfig.getListener()) {
                    ImageContainer imageContainer = new ImageContainer();
                    imageContainer.setmBitmap(bitmap);
                    defaultImageGetConfig.getListener().onResponse(imageContainer, b);
                }
            }

            @Override
            public void onError() {
                if (null != defaultImageGetConfig.getListener()) {
                    defaultImageGetConfig.getListener().onErrorResponse(new VolleyError("Failed"));
                }
            }
        });*/
    }

    public void _displayImageGif(final String uri, final ImageView imageView, final int defaultId, final int errorId) {
        ImageHelper.with()
                .load(uri)
                .view(imageView)
                .placeholder(defaultId)
                .error(errorId)
                .showload();
    }

    /**
     * 加载高斯模糊bitmap并设置给view显示
     *
     * @param context
     * @param uri
     * @param radius
     * @param listener
     */
    public void displayBlurImg(Context context, final Object uri, final View view, final int radius, final int defaultId, final int errorId, ImageListener listener) {
        this.displayBlurImg(context, uri, view, radius, 10, defaultId, errorId, listener);
    }

    /**
     * 加载高斯模糊bitmap并设置给view显示
     *
     * @param context
     * @param uri
     * @param radius   模糊度
     * @param sampling 图片采样取值系数。(采样后图片=width/sampling)
     * @param listener
     */
    public void displayBlurImg(final Context context, final Object uri, final View view, final int radius,
                               final int sampling, final int defaultId, final int errorId, final ImageListener listener) {
        if(null == uri)
            return;
        ImageLoadOptions.Builder builder = null;
        if(uri instanceof String)
            builder = ImageHelper.with(context).load((String) uri);
        else if(uri instanceof File)
            builder = ImageHelper.with(context).load((File) uri);
        else if(uri instanceof Integer)
            builder = ImageHelper.with(context).load((Integer) uri);
        
        if(null != builder) {

            ImageShowLoadCallback callback = null == listener ? null : new ImageShowLoadCallback() {
                @Override
                public void onLoadCompleted(Bitmap resource) {
                    if (null != listener) {
                        ImageContainer imageContainer = new ImageContainer();
                        imageContainer.setmBitmap(resource);
                        listener.onResponse(imageContainer, true);
                    }
                }

                @Override
                public void onLoadCompleted(Drawable resource) {
                    if (null != listener && resource instanceof BitmapDrawable) {
                        ImageContainer imageContainer = new ImageContainer();
                        imageContainer.setmBitmap(((BitmapDrawable)resource).getBitmap());
                        listener.onResponse(imageContainer, true);
                    }
                }

                @Override
                public void onLoadFailed() {
                    if (null != listener) {
                        listener.onErrorResponse(new VolleyError("Failed"));
                    }
                }
            };

            builder.view(view)
                    .placeholder(defaultId)
                    .error(errorId)
                    .blur(radius, sampling)
                    .callback(callback)
                    .showload();
        }
        
        
        
        /*ImageGetConfig.Builder builder = DefaultImageGetConfig.parseBuilder(ImageGet.defConfig)
                .setPlaceHolderResId(defaultId)
                .setErrorResId(errorId)
                .setBlur(true)
                .setRadius(radius)
                .setSampling(sampling)
                .setViewTarget(new ViewTarget<View, Bitmap>(view) {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        view.setBackground(new BitmapDrawable(context.getResources(), resource));
                    }

                    @Override
                    public void onLoadFailed(Exception e, Drawable errorDrawable) {
                        super.onLoadFailed(e, errorDrawable);
                        if (null != view) {
                            view.setBackground(errorDrawable);
                        }
                    }
                });
        final DefaultImageGetConfig defaultImageGetConfig = new DefaultImageGetConfig(builder);
        defaultImageGetConfig.setListener(listener);
        ImageGet.loadTarget(context, uri, defaultImageGetConfig, new LoaderListener() {
            @Override

            public void onSuccess(Bitmap bitmap, boolean b, boolean b1) {
                if (null != defaultImageGetConfig.getListener()) {
                    ImageContainer imageContainer = new ImageContainer();
                    imageContainer.setmBitmap(bitmap);
                    defaultImageGetConfig.getListener().onResponse(imageContainer, b);
                }
            }

            @Override
            public void onError() {
                if (null != defaultImageGetConfig.getListener()) {
                    defaultImageGetConfig.getListener().onErrorResponse(new VolleyError("Failed"));
                }
            }
        });*/
    }

    public void displayVeryImg(final String uri, final ImageView imageView, final ImageListener listener) {
        // TODO image view和 listener不能同时为null.
        if (TextUtils.isEmpty(uri) || (null == imageView && null == listener)) {
            if (null != listener) {
                listener.onErrorResponse(new VolleyError("error paramsters"));
            }
            return;
        }

        if (null == imageView && !TextUtils.isEmpty(uri) && null != listener) {//这时可能是只加载bitmap
            loadBitmap(App.getInstance().getApplicationContext(), uri, listener);
            return;
        }

        int width = 120;
        int height = 120;

        ImageShowLoadCallback callback = null == listener ? null : new ImageShowLoadCallback() {
            @Override
            public void onLoadCompleted(Bitmap resource) {
                if (null != listener) {
                    ImageContainer imageContainer = new ImageContainer();
                    imageContainer.setmBitmap(resource);
                    listener.onResponse(imageContainer, true);
                }
            }

            @Override
            public void onLoadCompleted(Drawable resource) {
                if (null != listener && resource instanceof BitmapDrawable) {
                    ImageContainer imageContainer = new ImageContainer();
                    imageContainer.setmBitmap(((BitmapDrawable)resource).getBitmap());
                    listener.onResponse(imageContainer, true);
                }
            }

            @Override
            public void onLoadFailed() {
                if (null != listener) {
                    listener.onErrorResponse(new VolleyError("Failed"));
                }
            }
        };

        ImageHelper.with()
                .override(width, height)
                .load(uri)
                .view(imageView)
                .callback(callback)
                .showload();
                

        /*ImageGetConfig.Builder builder = DefaultImageGetConfig.parseBuilder(ImageGet.defConfig)
                .setSize(new ImageGetConfig.OverrideSize(width, height));
        final DefaultImageGetConfig defaultImageGetConfig = new DefaultImageGetConfig(builder);
        defaultImageGetConfig.setListener(listener);

        ImageGet.loadImage(imageView, uri, defaultImageGetConfig, new LoaderListener() {
            @Override
            public void onSuccess(Bitmap bitmap, boolean b, boolean b1) {
                if (null != defaultImageGetConfig.getListener()) {
                    ImageContainer imageContainer = new ImageContainer();
                    imageContainer.setmBitmap(bitmap);
                    defaultImageGetConfig.getListener().onResponse(imageContainer, b);
                }
            }

            @Override
            public void onError() {
                if (null != defaultImageGetConfig.getListener()) {
                    defaultImageGetConfig.getListener().onErrorResponse(new VolleyError("Failed"));
                }
            }
        });*/
    }


    public void displayOriginalImg(final String uri, final ImageView imageView, final ImageListener listener) {
        // TODO image view和 listener不能同时为null.
        if (TextUtils.isEmpty(uri) || (null == imageView && null == listener)) {
            if (null != listener) {
                listener.onErrorResponse(new VolleyError("error paramsters"));
            }
            return;
        }
        this.displayOriginalImg(uri, imageView, ImageURLManager.FIX_WIDTH_AND_HEIGHT, listener);

    }

    public void displayOriginalImg(final String uri, final ImageView imageView, int type, final ImageListener listener) {
        // TODO image view和 listener不能同时为null.
        if (TextUtils.isEmpty(uri) || (null == imageView && null == listener)) {
            if (null != listener) {
                listener.onErrorResponse(new VolleyError("error paramsters"));
            }
            return;
        }

        String url = ImageURLManager.getRequestURL(uri, type);
        if (null == imageView && !TextUtils.isEmpty(url) && null != listener) {//这时可能是只加载bitmap
            loadBitmap(App.getInstance().getApplicationContext(), url, listener);
            return;
        }
    
        ImageProxyUrl.ClipType clipType = ImageProxyUrl.ClipType.FIX_WIDTH_AND_HEIGHT;
        for(ImageProxyUrl.ClipType c : ImageProxyUrl.ClipType.values()) {
            if(c.getValue() == type)
                clipType = c;
        }

        ImageShowLoadCallback callback = null == listener ? null : new ImageShowLoadCallback() {
            @Override
            public void onLoadCompleted(Bitmap resource) {
                if (null != listener) {
                    ImageContainer imageContainer = new ImageContainer();
                    imageContainer.setmBitmap(resource);
                    listener.onResponse(imageContainer, true);
                }
            }

            @Override
            public void onLoadCompleted(Drawable resource) {
                if (null != listener && resource instanceof BitmapDrawable) {
                    ImageContainer imageContainer = new ImageContainer();
                    imageContainer.setmBitmap(((BitmapDrawable)resource).getBitmap());
                    listener.onResponse(imageContainer, true);
                }
            }

            @Override
            public void onLoadFailed() {
                if (null != listener) {
                    listener.onErrorResponse(new VolleyError("Failed"));
                }
            }
        };
        
        ImageHelper.with(ImageProxyUrl.SizeType.CUSTOM_SIZE, clipType)
                .load(uri)
                .view(imageView)
                .callback(callback)
                .showload();

        /*ImageGetConfig.Builder builder = DefaultImageGetConfig.parseBuilder(ImageGet.defConfig);
        final DefaultImageGetConfig defaultImageGetConfig = new DefaultImageGetConfig(builder);
        defaultImageGetConfig.setListener(listener);

        ImageGet.loadImage(imageView, url, defaultImageGetConfig, new LoaderListener() {
            @Override
            public void onSuccess(Bitmap bitmap, boolean b, boolean b1) {
                if (null != defaultImageGetConfig.getListener()) {
                    ImageContainer imageContainer = new ImageContainer();
                    imageContainer.setmBitmap(bitmap);
                    defaultImageGetConfig.getListener().onResponse(imageContainer, b);
                }
            }

            @Override
            public void onError() {
                if (null != defaultImageGetConfig.getListener()) {
                    defaultImageGetConfig.getListener().onErrorResponse(new VolleyError("Failed"));
                }
            }
        });*/
    }

    public void displayNetworkImage(ImageLoader volleyImageLoader, final String uri, final NetworkImageView imageView, final int defaultId, final int errorId, final ImageURLManager.ImageStyle style, ImageListener listener) {
        int w = ImageURLManager.getWidth(style);
        int h = ImageURLManager.getHeight(style);
        this.displayNetworkImage(volleyImageLoader, uri, imageView, defaultId, errorId, w, h, ImageURLManager.FIX_WIDTH_AND_HEIGHT, listener);
    }

    public void displayNetworkImage(ImageLoader volleyImageLoader, final String uri,
                                    final NetworkImageView imageView, final int defaultId,
                                    final int errorId, final int w, final int h,
                                    final int clipType, final ImageListener listener) {
        if (defaultId != 0 && imageView != null) {
            imageView.setImageResource(defaultId);
        }

        // TODO image view和 listener不能同时为null.
        if (TextUtils.isEmpty(uri) || (null == imageView && null == listener)) {
            if (null != listener) {
                listener.onErrorResponse(new VolleyError("error paramsters"));
            }
            return;
        }

        int width = w;
        int height = h;
        if (width <= 0) {
            width = FrameConstant.SCREEN_WIDTH;
        }
        if (height <= 0) {
            height = FrameConstant.SCREEN_HEIGHT;
        }
    
        ImageProxyUrl.ClipType type = ImageProxyUrl.ClipType.FIX_WIDTH_AND_HEIGHT;
        for(ImageProxyUrl.ClipType c : ImageProxyUrl.ClipType.values()) {
            if(c.getValue() == clipType)
                type = c;
        }

        ImageShowLoadCallback callback = null == listener ? null : new ImageShowLoadCallback() {
            @Override
            public void onLoadCompleted(Bitmap resource) {
                if (null != listener) {
                    ImageContainer imageContainer = new ImageContainer();
                    imageContainer.setmBitmap(resource);
                    listener.onResponse(imageContainer, true);
                }
            }

            @Override
            public void onLoadCompleted(Drawable resource) {
                if (null != listener && resource instanceof BitmapDrawable) {
                    ImageContainer imageContainer = new ImageContainer();
                    imageContainer.setmBitmap(((BitmapDrawable)resource).getBitmap());
                    listener.onResponse(imageContainer, true);
                }
            }

            @Override
            public void onLoadFailed() {
                if (null != listener) {
                    listener.onErrorResponse(new VolleyError("Failed"));
                }
            }
        };

        ImageHelper.with(ImageProxyUrl.SizeType.CUSTOM_SIZE, type)
                .override(width, height)
                .load(uri)
                .view(imageView)
                .placeholder(defaultId)
                .error(errorId)
                .callback(callback)
                .showload();

        /*String url = ImageURLManager.getRequestURL(uri, width, height, clipType);

        if (null == imageView && !TextUtils.isEmpty(url) && null != listener) {//这时可能是只加载bitmap
            loadBitmap(App.getInstance().getApplicationContext(), url, listener);
            return;
        }

        ImageGetConfig.Builder builder = DefaultImageGetConfig.parseBuilder(ImageGet.defConfig)
                .setPlaceHolderResId(defaultId)
                .setErrorResId(errorId)
                .setSize(new ImageGetConfig.OverrideSize(width, height));
        final DefaultImageGetConfig defaultImageGetConfig = new DefaultImageGetConfig(builder);
        defaultImageGetConfig.setListener(listener);
        ImageGet.loadImage(imageView, url, defaultImageGetConfig, new LoaderListener() {
            @Override
            public void onSuccess(Bitmap bitmap, boolean b, boolean b1) {
                if (null != defaultImageGetConfig.getListener()) {
                    ImageContainer imageContainer = new ImageContainer();
                    imageContainer.setmBitmap(bitmap);
                    defaultImageGetConfig.getListener().onResponse(imageContainer, b);
                }
            }

            @Override
            public void onError() {
                if (null != defaultImageGetConfig.getListener()) {
                    defaultImageGetConfig.getListener().onErrorResponse(new VolleyError("Failed"));
                }
            }
        });*/
    }

    /**
     * 加载圆形图片，不带边框
     *
     * @param uri
     * @param view
     * @param defaultId
     * @param errorId
     * @param listener
     */
    public void displayCircleImg(final String uri, final ImageView view, final int defaultId, final int errorId, int width, int height, ImageListener listener) {
        this.displayCircleImg(uri, view, false, 0, 0, defaultId, errorId, width, height, listener);
    }

    /**
     * 加载圆形图片，带边框
     *
     * @param uri
     * @param view
     * @param isBorder
     * @param borderWidth
     * @param borderColor
     * @param defaultId
     * @param errorId
     * @param listener
     */
    public void displayCircleImg(final String uri, final ImageView view, boolean isBorder, final float borderWidth,
                                 final int borderColor, final int defaultId,
                                 final int errorId, int width, int height, final ImageListener listener) {
        if (TextUtils.isEmpty(uri) || (null == view && null == listener)) {
            if (null != listener) {
                listener.onErrorResponse(new VolleyError("error paramsters"));
            }
            return;
        }

        if (width <= 0) {
            width = FrameConstant.SCREEN_WIDTH;
        }
        if (height <= 0) {
            height = FrameConstant.SCREEN_HEIGHT;
        }

        ImageShowLoadCallback callback = null == listener ? null : new ImageShowLoadCallback() {
            @Override
            public void onLoadCompleted(Bitmap resource) {
                if (null != listener) {
                    ImageContainer imageContainer = new ImageContainer();
                    imageContainer.setmBitmap(resource);
                    listener.onResponse(imageContainer, true);
                }
            }

            @Override
            public void onLoadCompleted(Drawable resource) {
                if (null != listener && resource instanceof BitmapDrawable) {
                    ImageContainer imageContainer = new ImageContainer();
                    imageContainer.setmBitmap(((BitmapDrawable)resource).getBitmap());
                    listener.onResponse(imageContainer, true);
                }
            }

            @Override
            public void onLoadFailed() {
                if (null != listener) {
                    listener.onErrorResponse(new VolleyError("Failed"));
                }
            }
        };
    
        ImageHelper.with(ImageProxyUrl.SizeType.CUSTOM_SIZE, ImageProxyUrl.ClipType.FIX_WIDTH_AND_HEIGHT)
                .override(width, height)
                .load(uri)
                .view(view)
                .cropCircle()
                .placeholder(defaultId)
                .error(errorId)
                .callback(callback)
                .showload();
        
        /*String url = ImageURLManager.getRequestURL(uri, width, height, ImageURLManager.FIX_WIDTH_AND_HEIGHT);

        if (null == view && !TextUtils.isEmpty(url) && null != listener) {//这时可能是只加载bitmap
            loadBitmap(App.getInstance().getApplicationContext(), url, listener);
            return;
        }


        ImageGetConfig.Builder builder = DefaultImageGetConfig.parseBuilder(ImageGet.defConfig)
                .setPlaceHolderResId(defaultId)
                .setErrorResId(errorId)
                .setCropCircle(true)
                .setBorder(isBorder)
                .setBorderWidth(borderWidth)
                .setBorderColor(borderColor)
                .setSize(new ImageGetConfig.OverrideSize(width, height));

        final DefaultImageGetConfig defaultImageGetConfig = new DefaultImageGetConfig(builder);
        defaultImageGetConfig.setListener(listener);
        ImageGet.loadImage(view, url, defaultImageGetConfig, new LoaderListener() {
            @Override

            public void onSuccess(Bitmap bitmap, boolean b, boolean b1) {
                if (null != defaultImageGetConfig.getListener()) {
                    ImageContainer imageContainer = new ImageContainer();
                    imageContainer.setmBitmap(bitmap);
                    defaultImageGetConfig.getListener().onResponse(imageContainer, b);
                }
            }

            @Override
            public void onError() {
                if (null != defaultImageGetConfig.getListener()) {
                    defaultImageGetConfig.getListener().onErrorResponse(new VolleyError("Failed"));
                }
            }
        });*/
    }
}
