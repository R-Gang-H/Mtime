package com.mtime.base.imageload;

/**
 * Created by ZhouSuQiang on 2017/10/25.
 * 图片加载类，为了方便使用进ImageLoadStrategyManager进行进一步的代理
 */

class ImageLoader extends ImageLoadOptions.Builder {
    
    protected ImageLoader(Object context) {
        super(context);
    }
    
    @Override
    public void showload() {
        ImageLoadStrategyManager.getInstance().loadImage(this.build());
        resetParams();
    }
    
    @Override
    public void download() {
        ImageLoadStrategyManager.getInstance().download(this.build());
        resetParams();
    }
    
    public void resetParams() {
        mContext = null;
        mHolderDrawableRes = 0;
        mViewContainer = null;
        mImageSize.setSize(0, 0);
        mErrorDrawableRes = 0;
        mAsGif = false;
        mIsSkipMemoryCache = false;
        mBlurImage = false;
        mBlurRadius = 25;
        mBlurSampling = 1;
        mIsCropCircle = false;
        mDiskCacheStrategy = ImageLoadOptions.DiskCacheStrategy.DEFAULT;
        mCallback = null;
        mRoundedCornersMargin = 0;
        mRoundedCornersRadius = 0;
        mRoundedCornerType = 0;
        mIsRoundedCorners = false;
        mThumbnailScale = 0;
        mThumbnailUrl = null;
    }
}
