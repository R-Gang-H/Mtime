package com.mtime.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * 本地图片加载器,采用的是异步解析本地图片，单例模式利用getInstance()获取NativeImageLoader实例
 * 调用loadNativeImage()方法加载本地图片，此类可作为一个加载本地图片的工具类
 */
public class NativeImageLoader {
    //    private LruCache<String, Bitmap> mMemoryCache;
    private final static NativeImageLoader mInstance = new NativeImageLoader();
    private final ExecutorService mImageThreadPool = Executors.newFixedThreadPool(1);

    private NativeImageLoader() {
        // 获取应用程序的最大内存
//        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
//
//        // 用最大内存的1/4来存储图片
//        final int cacheSize = maxMemory / 8;
//        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
//
//            // 获取每张图片的大小
//            @Override
//            protected int sizeOf(String key, Bitmap bitmap) {
//                return bitmap.getRowBytes() * bitmap.getHeight() / 1024;
//            }
//        };
    }

    public interface ImageLoaderCallBack {
        void onImageLoader(Bitmap bitmap, String path);
    }

    /**
     * 通过此方法来获取NativeImageLoader的实例
     *
     * @return
     */
    public static NativeImageLoader getInstance() {
        return mInstance;
    }

    /**
     * 加载本地图片，对图片不进行裁剪
     *
     * @param path
     * @param mCallBack
     * @return
     */
//    public Bitmap loadNativeImage(final String path,
//                                  final ImageLoaderCallBack mCallBack) {
//        return this.loadNativeImage(path, null, mCallBack);
//    }

    /**
     * 此方法来加载本地图片，这里的mPoint是用来封装ImageView的宽和高，我们会根据ImageView控件的大小来裁剪Bitmap
     * 如果你不想裁剪图片，调用loadNativeImage(final String path, final NativeImageCallBack
     * mCallBack)来加载
     *
     * @param path
     * @param mPoint
     * @param mCallBack
     * @return
     */
//    public Bitmap loadNativeImage(final String path, final Point mPoint,
//                                  final ImageLoaderCallBack mCallBack) {
//
//        ImageGet.loadBitmap(App.getInstance().getApplicationContext(), path, new BitmapLoadingListener() {
//            @Override
//            public void onSuccess(Bitmap bitmap) {
//                if (null != mCallBack) {
//                    mCallBack.onImageLoader(bitmap, path);
//                }
//            }
//
//            @Override
//            public void onError() {
//            }
//        });
//
//        return null;
//
//    }

    public Bitmap loadNativeImage(final String path, final int scaleSize,
                                  final ImageLoaderCallBack mCallBack) {

        final Handler mHander = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                mCallBack.onImageLoader((Bitmap) msg.obj, path);
            }

        };

        // 启用线程去加载本地的图片
        mImageThreadPool.execute(new Runnable() {

            @Override
            public void run() {
                // 先获取图片的缩略图
                Bitmap mBitmap = decodeThumbBitmapForFile(path,
                        scaleSize);
                Message msg = mHander.obtainMessage();
                msg.obj = mBitmap;
                mHander.sendMessage(msg);
            }
        });

        return null;

    }

//    public Bitmap loadNativeImage(final String path, final String tag, final Point mPoint,
//                                  final ImageLoaderCallBack mCallBack) {
//        this.loadNativeImage(path, mPoint, mCallBack);
//
//        return null;
//
//    }

    /**
     * 根据View(主要是ImageView)的宽和高来获取图片的缩略图
     *
     * @param path
     * @param viewWidth
     * @param viewHeight
     * @return
     */
    private Bitmap decodeThumbBitmapForFile(String path, int viewWidth,
                                            int viewHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        // 设置为true,表示解析Bitmap对象，该对象不占内存
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        // 设置缩放比例
        options.inSampleSize = computeScale(options, viewWidth, viewHeight);

        // 设置为false,解析Bitmap对象加入到内存中
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(path, options);
    }

    private Bitmap decodeThumbBitmapForFile(String path, int inSampleSize
    ) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        // 设置为true,表示解析Bitmap对象，该对象不占内存
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        // 设置缩放比例
        options.inSampleSize = inSampleSize;

        // 设置为false,解析Bitmap对象加入到内存中
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(path, options);
    }

    //    /**
//     * 根据View(主要是ImageView)的宽和高来计算Bitmap缩放比例。默认不缩放
//     *
//     * @param options
//     * @param viewWidth
//     * @param viewHeight
//     */
//    private int computeScale(BitmapFactory.Options options, int viewWidth,
//                             int viewHeight) {
//        int inSampleSize = 1;
//        if (viewWidth == 0 || viewWidth == 0) {
//            return inSampleSize;
//        }
//        int bitmapWidth = options.outWidth;
//        int bitmapHeight = options.outHeight;
//
//        // 假如Bitmap的宽度或高度大于我们设定图片的View的宽高，则计算缩放比例
//        if (bitmapWidth > viewWidth || bitmapHeight > viewWidth) {
//            int widthScale = Math
//                    .round((float) bitmapWidth / (float) viewWidth);
//            int heightScale = Math.round((float) bitmapHeight
//                    / (float) viewWidth);
//
//            // 为了保证图片不缩放变形，我们取宽高比例最小的那个
//            inSampleSize = widthScale < heightScale ? widthScale : heightScale;
//        }
//        return inSampleSize;
//    }
    public void cleanBitmap() {
//        if (mMemoryCache != null) {
//            if (mMemoryCache.size() > 0) {
//                mMemoryCache.evictAll();
//            }
//        }
    }

    private int computeSize(BitmapFactory.Options options, int viewWidth,
                            int viewHeight) {
        if (viewWidth == 0 || viewWidth == 0) {
            return 1;
        }

        int bitmapWidth = options.outWidth;
        int bitmapHeight = options.outHeight;
        int sizeLowerBound = (int) Math.ceil(Math
                .sqrt(bitmapWidth * bitmapHeight / (viewWidth * viewHeight)));
        int sizeUpperBound = 1;

        // 假如Bitmap的宽度或高度大于我们设定图片的View的宽高，则计算缩放比例
        if (bitmapWidth > viewWidth || bitmapHeight > viewWidth) {
            int widthScale = Math
                    .round((float) bitmapWidth / (float) viewWidth);
            int heightScale = Math.round((float) bitmapHeight
                    / (float) viewWidth);

            // 为了保证图片不缩放变形，我们取宽高比例最小的那个
            sizeUpperBound = widthScale < heightScale ? widthScale : heightScale;
        }

        if (sizeUpperBound < sizeLowerBound) {
            return sizeLowerBound;
        }
        return sizeUpperBound;
    }

    /**
     * 根据View(主要是ImageView)的宽和高来计算Bitmap缩放比例
     *
     * @param options
     * @param viewWidth
     * @param viewHeight
     */
    private int computeScale(BitmapFactory.Options options, int viewWidth,
                             int viewHeight) {
        int size = computeSize(options, viewWidth, viewHeight);

        int roundSize;
        if (size <= 8) {
            roundSize = 1;
            while (roundSize < size) {
                roundSize <<= 1;
            }
        } else {
            roundSize = (size + 7) / 8 * 8;
        }

        return roundSize;
    }
}
