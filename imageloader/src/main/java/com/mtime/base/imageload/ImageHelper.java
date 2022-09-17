package com.mtime.base.imageload;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.kotlin.android.image.ImageCallback;
import com.kotlin.android.image.coil.ext.CoilCompat;
import com.kotlin.android.ktx.ext.dimension.DimensionExtKt;
import com.mtime.base.imageload.ImageProxyUrl.ClipType;
import com.mtime.base.imageload.ImageProxyUrl.SizeType;

import org.jetbrains.annotations.Nullable;
import org.xmlpull.v1.XmlPullParser;

import java.io.File;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;



/**
 * Created by ZhouSuQiang on 2017/10/25.
 * 图片加载类
 */

@Deprecated()
public final class ImageHelper extends ImageLoader {
    protected static volatile ImageLoader sInstance;
    private static float scale = 0;

    private SizeType mSizeType;
    private ClipType mClipType;

    private ImageHelper(Object context, SizeType sizeType, ClipType clipType) {
        super(context);
        this.mSizeType = sizeType;
        this.mClipType = clipType;
    }

    public static void init(Context context, IImageLoadStrategy.Config config) {
//        ImageLoadStrategyManager.getInstance().init(context, config);
        scale = context.getResources().getDisplayMetrics().density;
        installAppCompat(context);
    }

    private static void installAppCompat(Context context) {
        try {
            Class<?> delegateClass = Class.forName("androidx.appcompat.widget.ResourceManagerInternal$InflateDelegate");
            Object delegateIns = Proxy.newProxyInstance(context.getClassLoader(), new Class[]{delegateClass},
                    new InvocationHandler() {
                        @Override
                        public Object invoke(Object proxy, Method method, Object[] args) {
                            try {

                                Context c = (Context) args[0];
                                return LogoDrawable.createFromXmlInner(c.getResources(), (XmlPullParser) args[1],
                                        (AttributeSet) args[2], (Resources.Theme) args[3]);
                            } catch (Throwable e) {
                                e.printStackTrace();
                            }
                            return null;
                        }
                    });
            Class<?> appcompat = Class.forName("androidx.appcompat.widget.ResourceManagerInternal");
            Method get = appcompat.getMethod("get");

            Object appcompatIns = get.invoke(null);

            Method addDelegate = appcompat.getDeclaredMethod("addDelegate", String.class, delegateClass);
            addDelegate.setAccessible(true);

            addDelegate.invoke(appcompatIns, "LogoDrawable", delegateIns);
            if (Build.VERSION.SDK_INT < 24) {
                addDelegate.invoke(appcompatIns, LogoDrawable.class.getName(), delegateIns);
            }

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    protected static ImageLoadOptions.Builder getInstance(Object context, SizeType sizeType, ClipType clipType) {
        if (sInstance == null) {
            synchronized (ImageHelper.class) {
                if (sInstance == null) {
                    sInstance = new ImageHelper(context, sizeType, clipType);
                    return sInstance;
                }
            }
        }

        sInstance.resetParams();
        sInstance.mContext = context;
        ((ImageHelper) sInstance).mSizeType = sizeType;
        ((ImageHelper) sInstance).mClipType = clipType;
        return sInstance;
    }


    public static ImageLoadOptions.Builder with(Context context, SizeType sizeType, ClipType clipType) {
        return getInstance(context, sizeType, clipType);
    }

    public static ImageLoadOptions.Builder with(Activity activity, SizeType sizeType, ClipType clipType) {
        return getInstance(activity, sizeType, clipType);
    }

    public static ImageLoadOptions.Builder with(FragmentActivity activity, SizeType sizeType, ClipType clipType) {
        return getInstance(activity, sizeType, clipType);
    }

    public static ImageLoadOptions.Builder with(Fragment v4Fragment, SizeType sizeType, ClipType clipType) {
        return getInstance(v4Fragment, sizeType, clipType);
    }

    public static ImageLoadOptions.Builder with(android.app.Fragment fragment, SizeType sizeType, ClipType clipType) {
        return getInstance(fragment, sizeType, clipType);
    }

    public static ImageLoadOptions.Builder with(SizeType sizeType, ClipType clipType) {
        return getInstance(null, sizeType, clipType);
    }

    public static ImageLoadOptions.Builder with(Context context) {
        return getInstance(context, null, null);
    }

    public static ImageLoadOptions.Builder with(Activity activity) {
        return getInstance(activity, null, null);
    }

    public static ImageLoadOptions.Builder with(FragmentActivity activity) {
        return getInstance(activity, null, null);
    }

    public static ImageLoadOptions.Builder with(Fragment v4Fragment) {
        return getInstance(v4Fragment, null, null);
    }

    public static ImageLoadOptions.Builder with(android.app.Fragment fragment) {
        return getInstance(fragment, null, null);
    }

    public static ImageLoadOptions.Builder with() {
        return getInstance(null, null, null);
    }

    private int px2dp(float pxValue) {
        if (pxValue == 0) {
            return 0;
        } else {
            return (int) (pxValue / scale + 0.5f);
        }
    }

    @Override
    public void showload() {
//        if (mLoad instanceof String) {
//            mLoad = ImageProxyUrl.createProxyUrl((String) mLoad, mImageSize, mSizeType, mClipType);
//        }
//        super.showload();

        if (!mBlurImage) {
            mImageSize.width = px2dp(mImageSize.width);
            mImageSize.height = px2dp(mImageSize.height);
        }

        if (mViewContainer instanceof ImageView) {
            if (mLoad instanceof String) {
                if (null != mCallback) {
                    CoilCompat.INSTANCE.loadGifImageCallback(
                            mLoad,
                            DimensionExtKt.getDp(mImageSize.width),
                            DimensionExtKt.getDp(mImageSize.height),
                            mImageSize.width > 0,
                            true,
                            new ImageCallback() {
                                @Override
                                public void onStart(@Nullable Drawable placeholder) {
                                }

                                @Override
                                public void onError(@Nullable Drawable error) {
                                    if (null != mViewContainer) {
                                        ((ImageView) mViewContainer).setImageDrawable(error);
                                    }
                                    mCallback.onLoadFailed();
                                }

                                @Override
                                public void onSuccess(@Nullable Drawable drawable) {
                                    if (null != mViewContainer) {
                                        ((ImageView) mViewContainer).setImageDrawable(drawable);
                                    }
                                    mCallback.onLoadCompleted(drawable);
                                    if (drawable instanceof BitmapDrawable) {
                                        mCallback.onLoadCompleted(((BitmapDrawable) drawable).getBitmap());
                                    }
                                }
                            }
                    );
                } else {
                    if (mAsGif) {
                        CoilCompat.INSTANCE.loadGifImage(
                                (ImageView) mViewContainer,
                                (String) mLoad,
                                DimensionExtKt.getDp(mImageSize.width),
                                DimensionExtKt.getDp(mImageSize.height),
                                mImageSize.width > 0,
                                true,
                                mIsCropCircle,
                                null,
                                null,
                                0F
                        );
                    } else {
                        if (mBlurImage) {
                            CoilCompat.INSTANCE.loadBlurImage(
                                    (ImageView) mViewContainer,
                                    (String) mLoad,
                                    DimensionExtKt.getDp(mImageSize.width),
                                    DimensionExtKt.getDp(mImageSize.height),
                                    mImageSize.width > 0,
                                    mHolderDrawableRes,
                                    mBlurRadius,
                                    mBlurSampling
                            );
                        } if (mIsRoundedCorners) {
                            CoilCompat.INSTANCE.loadGifImage(
                                    (ImageView) mViewContainer,
                                    (String) mLoad,
                                    DimensionExtKt.getDp(mImageSize.width),
                                    DimensionExtKt.getDp(mImageSize.height),
                                    mImageSize.width > 0,
                                    true,
                                    mIsCropCircle,
                                    mHolderDrawableRes,
                                    null,
                                    mRoundedCornersRadius
                            );
                        } else {
                            CoilCompat.INSTANCE.loadGifImage(
                                    (ImageView) mViewContainer,
                                    mLoad,
                                    DimensionExtKt.getDp(mImageSize.width),
                                    DimensionExtKt.getDp(mImageSize.height),
                                    mImageSize.width > 0,
                                    true,
                                    mIsCropCircle,
                                    mHolderDrawableRes,
                                    null,
                                    0F
                            );
                        }
                    }
                }
            } else if (mLoad instanceof Integer) {
                CoilCompat.INSTANCE.loadGifImage(
                        (ImageView) mViewContainer,
                        mLoad,
                        0,
                        0,
                        false,
                        true,
                        false,
                        null,
                        null,
                        0F
                );
            } else if (mLoad instanceof File) {
                CoilCompat.INSTANCE.loadGifImage(
                        (ImageView) mViewContainer,
                        (File) mLoad,
                        0,
                        0,
                        false,
                        true,
                        false,
                        mHolderDrawableRes,
                        null,
                        0F
                );
            }
        } else {
            if (null != mViewContainer) {
                mViewContainer.setBackgroundResource(mHolderDrawableRes);
            }
            CoilCompat.INSTANCE.loadGifImageCallback(
                    mLoad,
                    mImageSize.width,
                    mImageSize.height,
                    mImageSize.width > 0 && mLoad instanceof String,
                    true,
                    new ImageCallback() {
                        @Override
                        public void onStart(@Nullable Drawable placeholder) {

                        }

                        @Override
                        public void onError(@Nullable Drawable error) {
                            if (null != mViewContainer) {
                                mViewContainer.setBackgroundResource(mHolderDrawableRes);
                            }
                            if (null != mCallback) {
                                mCallback.onLoadFailed();
                            }
                        }

                        @Override
                        public void onSuccess(@Nullable Drawable drawable) {
                            if (null != mViewContainer) {
                                mViewContainer.setBackground(drawable);
                            }
                            if (null != mCallback) {
                                mCallback.onLoadCompleted(drawable);
                                if (drawable instanceof BitmapDrawable) {
                                    mCallback.onLoadCompleted(((BitmapDrawable) drawable).getBitmap());
                                }
                            }
                        }
                    }
            );
        }
    }

    @Override
    public void download() {
        if (mLoad instanceof String) {
            mLoad = ImageProxyUrl.createProxyUrl((String) mLoad, mImageSize, mSizeType, mClipType);
        }
        super.download();
    }

    @Override
    public void resetParams() {
        super.resetParams();
        mSizeType = null;
        mClipType = null;
    }

}
