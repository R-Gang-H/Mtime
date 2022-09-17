package com.mtime.frame.holder;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

import com.kk.taurus.uiframe.d.BaseState;
import com.kk.taurus.uiframe.v.BaseLoadingHolder;
import com.mtime.R;
import com.mtime.base.imageload.ImageHelper;
import com.mtime.base.imageload.ImageShowLoadCallback;
import com.mtime.bussiness.splash.LoadManager;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import pl.droidsonroids.gif.GifImageView;

/**
 * Created by mtime on 2017/10/9.
 */

public class LoadingHolder extends BaseLoadingHolder {

    private View mDefaultLoading;
    private View mADLoading;
    private ImageView mLoadingImage;
    private GifImageView mGifView;
    private AnimationDrawable animationDrawable;
    private Drawable gbDrawable;

    public LoadingHolder(Context context) {
        super(context);
    }

    @Override
    public void onCreate() {
        setContentView(R.layout.layout_loading);
        mDefaultLoading = getViewById(R.id.ll_default_loading);
        mADLoading = getViewById(R.id.ll_ad_loading);
        mLoadingImage = getViewById(R.id.loading_img);
        mGifView = getViewById(R.id.loading_img_gif);
//        handleLoadingIcon();
    }


//    private void startLoading() {
//        ImageHelper.with()
//                .view(mGifView)
//                .load(LoadManager.getLoadIcon())
//                .asGif()
//                .callback(new ImageShowLoadCallback() {
//                    @Override
//                    public void onLoadCompleted(Bitmap bitmap) {
//                    }
//
//                    @Override
//                    public void onLoadCompleted(Drawable resource) {
//
//                        mADLoading.setBackground(null);
//                        mADLoading.setVisibility(View.VISIBLE);
//                        mDefaultLoading.setVisibility(View.GONE);
//                        stopAnimationDrawable();
//                    }
//
//                    @Override
//                    public void onLoadFailed() {
//                        mDefaultLoading.setVisibility(View.VISIBLE);
//                        mADLoading.setVisibility(View.GONE);
//                        stopGifDrawable();
//                        //start default
//                        animationDrawable = (AnimationDrawable) mLoadingImage.getDrawable();
//                        animationDrawable.start();
//                    }
//                })
//                .showload();
////            mGifView.setImageDrawable(gbDrawable);
//    }

    private void stopGifDrawable() {
        if (gbDrawable != null) {
            //stop ad gif
//            gbDrawable.stop();
        }
    }

    private void stopAnimationDrawable() {
        //stop default
        if (animationDrawable != null) {
            animationDrawable.stop();
        }
    }

    private void stopLoading() {
        stopGifDrawable();
        stopAnimationDrawable();
    }

    @Override
    public void setState(BaseState state) {
        super.setState(state);
//        if (state == BaseState.LOADING || state == BaseState.LOADING_WITH_CONTENT) {
//            startLoading();
//        } else {
//            stopLoading();
//        }
    }

    // 将Bitmap转换成InputStream
    public InputStream Bitmap2InputStream(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        InputStream is = new ByteArrayInputStream(baos.toByteArray());
        return is;
    }

    // 将Bitmap转换成InputStream
    public InputStream Bitmap2InputStream(Bitmap bm, int quality) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, quality, baos);
        InputStream is = new ByteArrayInputStream(baos.toByteArray());
        return is;
    }
}
