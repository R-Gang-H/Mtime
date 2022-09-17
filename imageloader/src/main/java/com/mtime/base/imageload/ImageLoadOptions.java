package com.mtime.base.imageload;

import android.graphics.drawable.Drawable;
import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import android.view.View;

import java.io.File;

/**
 * Created by Administrator on 2017/3/20 0020.
 */
public class ImageLoadOptions {
    public Object mContext;
    
    public View mViewContainer;  //图片容器
    public Object mLoad; //图片地址(文件、url、资源id)
    public int mHolderDrawableRes;  // 设置展位图
    public Drawable mHolderDrawable;  // 设置展位图
    public int mErrorDrawableRes;  //是否展示加载错误的图片
    public Drawable mErrorDrawable;  //是否展示加载错误的图片
    public ImageSize mImageSize;  //设置图片的大小
    public boolean mAsGif = false;   //是否作为gif展示
    public boolean mIsSkipMemoryCache = false; //是否跳过内存缓存
    public DiskCacheStrategy mDiskCacheStrategy = DiskCacheStrategy.SOURCE; //磁盘缓存策略
    public boolean mBlurImage = false; //是否使用高斯模糊
    public int mBlurRadius; //高斯模糊,模糊度
    public int mBlurSampling; //高斯模糊,缩放系数。(width/sampling)
    public boolean mIsCropCircle; //是否裁剪为圆形
    public boolean mIsRoundedCorners; //是否剪切圆角
    public int mRoundedCornersRadius;
    public int mRoundedCornersMargin;
    public float mThumbnailScale; //缩略图缩放比例
    public String mThumbnailUrl; //缩略图URL
    public int mRoundedCornerType;
    public IImageLoadCallback mCallback = null; //
    
    ImageLoadOptions(Builder builder) {
        this.mAsGif = builder.mAsGif;
        this.mErrorDrawableRes = builder.mErrorDrawableRes;
        this.mHolderDrawableRes = builder.mHolderDrawableRes;
        this.mErrorDrawable = builder.mErrorDrawable;
        this.mHolderDrawable = builder.mHolderDrawable;
        this.mImageSize = builder.mImageSize;
        this.mIsSkipMemoryCache = builder.mIsSkipMemoryCache;
        this.mDiskCacheStrategy = builder.mDiskCacheStrategy;
        this.mLoad = builder.mLoad;
        this.mViewContainer = builder.mViewContainer;
        this.mBlurImage = builder.mBlurImage;
        this.mBlurRadius = builder.mBlurRadius;
        this.mBlurSampling = builder.mBlurSampling;
        this.mIsCropCircle = builder.mIsCropCircle;
        this.mCallback = builder.mCallback;
        this.mContext = builder.mContext;
        this.mIsRoundedCorners = builder.mIsRoundedCorners;
        this.mRoundedCornersRadius = builder.mRoundedCornersRadius;
        this.mRoundedCornersMargin = builder.mRoundedCornersMargin;
        this.mRoundedCornerType = builder.mRoundedCornerType;
        this.mThumbnailScale = builder.mThumbnailScale;
        this.mThumbnailUrl = builder.mThumbnailUrl;
    }
    
    public static abstract class Builder {
        protected Object mContext;
        protected int mHolderDrawableRes = 0;  // 设置展位图
        protected Drawable mHolderDrawable;  // 设置展位图
        protected int mErrorDrawableRes = 0;  //是否展示加载错误的图片
        protected Drawable mErrorDrawable;  //是否展示加载错误的图片
        protected View mViewContainer;  // 图片容器
        protected Object mLoad; //图片地址(文件、url、资源id)
        protected ImageSize mImageSize = new ImageSize(0, 0);  //设置图片的大小
        protected boolean mAsGif = false;   //是否作为gif展示
        protected boolean mIsSkipMemoryCache = false; //是否跳过内存缓存
        protected boolean mBlurImage = false; //是否使用高斯模糊
        protected int mBlurRadius = 25; //高斯模糊,模糊度
        protected int mBlurSampling = 1; //高斯模糊,缩放系数。(width/sampling)
        protected boolean mIsCropCircle = false; //是否裁剪为圆形
        protected boolean mIsRoundedCorners; //是否剪切圆角
        protected int mRoundedCornersRadius;
        protected int mRoundedCornersMargin;
        protected int mRoundedCornerType;
        protected float mThumbnailScale; //缩略图缩放比例
        protected String mThumbnailUrl; //缩略图URL
        protected DiskCacheStrategy mDiskCacheStrategy = DiskCacheStrategy.SOURCE; //磁盘缓存策略
        protected IImageLoadCallback mCallback = null; //
        
        public Builder(Object context) {
            this.mContext = context;
        }
        
        public Builder view(View view) {
            this.mViewContainer = view;
            if(null == mContext && null != view) {
                mContext = view.getContext().getApplicationContext();
            }
            return this;
        }
        
        public Builder load(String url) {
            this.mLoad = url;
            return this;
        }
        
        public Builder load(@DrawableRes int resId) {
            this.mLoad = resId;
            return this;
        }
        
        public Builder load(File file) {
            this.mLoad = file;
            return this;
        }
    
        public Builder placeholder(@DrawableRes int holderDrawable) {
            this.mHolderDrawableRes = holderDrawable;
            return this;
        }
    
        public Builder placeholder(@Nullable Drawable holderDrawable) {
            this.mHolderDrawable = holderDrawable;
            return this;
        }
    
        /**
         * radius "23"：设置模糊度(在0.0到25.0之间)，默认”25";
         * sampling "4":图片缩放比例
         */
        public Builder blur(int radius, int sampling) {
            this.mBlurImage = true;
            this.mBlurRadius = radius;
            this.mBlurSampling = sampling;
            return this;
        }
        
        public Builder skipMemoryCache() {
            this.mIsSkipMemoryCache = true;
            return this;
            
        }
    
        public Builder cropCircle() {
            this.mIsCropCircle = true;
            return this;
        }
    
        public Builder override(int width, int height) {
            this.mImageSize.width = width;
            this.mImageSize.height = height;
            return this;
        }
        
        public Builder asGif() {
            this.mAsGif = true;
            return this;
        }
        
        public Builder error(@DrawableRes int errorDrawable) {
            this.mErrorDrawableRes = errorDrawable;
            return this;
        }
        
        public Builder error(@Nullable Drawable errorDrawable) {
            this.mErrorDrawable = errorDrawable;
            return this;
        }
        
        public Builder callback(IImageLoadCallback callback) {
            this.mCallback = callback;
            return this;
        }
        
        public Builder diskCacheStrategy(DiskCacheStrategy mDiskCacheStrategy) {
            this.mDiskCacheStrategy = mDiskCacheStrategy;
            return this;
            
        }
    
        /**
         * 裁剪圆角处理
         * @param radius 圆角大小
         * @param margin 就是margin，内移
         * @return
         */
        public Builder roundedCorners(int radius, int margin) {
            this.mIsRoundedCorners = true;
            this.mRoundedCornersRadius = radius;
            this.mRoundedCornersMargin = margin;
            this.mRoundedCornerType = 0;
            return this;
        }
        
        public Builder thumbnailScale(float scale) {
            this.mThumbnailScale = scale;
            return this;
        }
        
        public Builder thumbnailUrl(String url) {
            this.mThumbnailUrl = url;
            return this;
        }
        
        
        public ImageLoadOptions build() {
            return new ImageLoadOptions(this);
        }
        
        public abstract void showload();
        
        public abstract void download();
    }
    
    //对应重写图片size
    public final static class ImageSize {
        public int width = 0;
        public int height = 0;
        
        public ImageSize(int width, int heigh) {
            setSize(width, heigh);
        }
        
        public void setSize(int width, int heigh) {
            this.width = width;
            this.height = heigh;
        }
        
        public int getHeight() {
            return height;
        }
        
        public int getWidth() {
            return width;
        }
    }
    
    //对应磁盘缓存策略
    public enum DiskCacheStrategy {
        All, NONE, SOURCE, RESULT, DEFAULT
    }
    
}
