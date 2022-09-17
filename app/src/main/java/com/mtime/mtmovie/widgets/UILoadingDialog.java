package com.mtime.mtmovie.widgets;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.mtime.R;
import com.mtime.base.imageload.ImageHelper;
import com.mtime.base.imageload.ImageShowLoadCallback;
import com.mtime.bussiness.splash.LoadManager;


/**
 * 加载 Dialog
 */
public class UILoadingDialog extends Dialog {

    private GifDrawable mGifDrawable;
//    private AnimationDrawable mAnimDrawable;
    private TextView mLoadingTxt;
    private final Context mContext;

    public UILoadingDialog(Context context, int theme) {
        super(context, theme);
        this.mContext = context;
    }

    public UILoadingDialog(Context context) {
        super(context);
        this.mContext = context;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.uiutil_loading_dialog);
        mLoadingTxt = findViewById(R.id.loading_default_txt_tv);
        View animLayout = findViewById(R.id.loading_img_layout);
        ImageView animView = findViewById(R.id.loading_img_default);
        ImageView gifView = findViewById(R.id.loading_img);
//        mAnimDrawable = (AnimationDrawable) animView.getBackground();

//        if (!TextUtils.isEmpty(LoadManager.getLoadIcon())) {
//            animLayout.setVisibility(View.GONE);
//            gifView.setVisibility(View.VISIBLE);
//            ImageHelper.with()
//                    .view(gifView)
//                    .load(LoadManager.getLoadIcon())
//                    .asGif()
//                    .callback(new ImageShowLoadCallback() {
//                        @Override
//                        public void onLoadCompleted(Bitmap bitmap) {
//                        }
//
//                        @Override
//                        public void onLoadCompleted(Drawable resource) {
//                            mGifDrawable = (GifDrawable) resource;
//                            gifView.setImageDrawable(resource);
//                        }
//
//                        @Override
//                        public void onLoadFailed() {
//                            animLayout.setVisibility(View.VISIBLE);
//                            gifView.setVisibility(View.GONE);
//                            mAnimDrawable.start();
//                        }
//                    })
//                    .showload();
//        } else {
//            animLayout.setVisibility(View.VISIBLE);
//            gifView.setVisibility(View.GONE);
//            mAnimDrawable.start();
//        }
    }

    public void setShowTxt(String txt) {
        if (null != mLoadingTxt) {
            mLoadingTxt.setText(TextUtils.isEmpty(txt) ? "" : txt);
        }
    }

    public void setShowTxt(int res) {
        if (null != mLoadingTxt) {
            mLoadingTxt.setText(mContext.getString(res));
        }
    }

    public void recycle() {
        if (mGifDrawable != null) {
            mGifDrawable.stop();
            mGifDrawable.recycle();
        }
//        if (null != mAnimDrawable && mAnimDrawable.isRunning()) {
//            mAnimDrawable.stop();
//        }
    }

}
