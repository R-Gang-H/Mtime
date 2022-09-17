package com.mtime.frame.callback;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.kingja.loadsir.callback.Callback;
import com.mtime.R;
import com.mtime.base.imageload.ImageHelper;
import com.mtime.base.imageload.ImageShowLoadCallback;
import com.mtime.bussiness.splash.LoadManager;

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2019-05-10
 */
public class MtimeLoadingCallback extends Callback {

    private GifDrawable mGifDrawable;
//    private AnimationDrawable mAnimDrawable;
    private TextView mLoadingTxt;
    private View animLayout;
    private ImageView animView;
    private ImageView gifView;

    @Override
    protected int onCreateView() {
        return R.layout.uiutil_loading_dialog;
    }

    @Override
    protected void onViewCreate(Context context, View view) {
        super.onViewCreate(context, view);

        mLoadingTxt = view.findViewById(R.id.loading_default_txt_tv);
        animLayout = view.findViewById(R.id.loading_img_layout);
        animView = view.findViewById(R.id.loading_img_default);
        gifView = view.findViewById(R.id.loading_img);
//        mAnimDrawable = (AnimationDrawable) animView.getBackground();

    }

    /*public void start() {
        if (!TextUtils.isEmpty(LoadManager.getLoadIcon())) {
            animLayout.setVisibility(View.GONE);
            gifView.setVisibility(View.VISIBLE);
            ImageHelper.with()
                    .view(gifView)
                    .load(LoadManager.getLoadIcon())
                    .asGif()
                    .callback(new ImageShowLoadCallback() {
                        @Override
                        public void onLoadCompleted(Bitmap bitmap) {
                        }

                        @Override
                        public void onLoadCompleted(Drawable resource) {
                            mGifDrawable = (GifDrawable) resource;
                            gifView.setImageDrawable(resource);
                        }

                        @Override
                        public void onLoadFailed() {
                            animLayout.setVisibility(View.VISIBLE);
                            gifView.setVisibility(View.GONE);
                            mAnimDrawable.start();
                        }
                    })
                    .showload();
        } else {
            animLayout.setVisibility(View.VISIBLE);
            gifView.setVisibility(View.GONE);
            mAnimDrawable.start();
        }
    }*/

//    public void recycle() {
//        if (mGifDrawable != null) {
//            mGifDrawable.stop();
//            mGifDrawable.recycle();
//        }
//        if (null != mAnimDrawable && mAnimDrawable.isRunning()) {
//            mAnimDrawable.stop();
//        }
//    }

   /* @Override
    public void onAttach(Context context, View view) {
        super.onAttach(context, view);
        start();
    }*/

    /*@Override
    public void onDetach() {
        super.onDetach();
        recycle();
    }*/
}
