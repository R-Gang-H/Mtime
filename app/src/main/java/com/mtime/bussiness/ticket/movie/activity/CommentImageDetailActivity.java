package com.mtime.bussiness.ticket.movie.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;

import com.mtime.frame.BaseActivity;
import com.mtime.R;
import com.mtime.constant.FrameConstant;
import com.mtime.util.ImageLoader;
import com.mtime.util.ImageURLManager;
import com.mtime.util.VolleyError;
import com.mtime.widgets.photoview.CustomClickListener;
import com.mtime.widgets.photoview.PhotoView;

/**
 * Created by guangshun on 15-7-24.
 * 评论图片详情
 */
public class CommentImageDetailActivity extends BaseActivity {
    public static final String KEY_IMAGE_PATH = "image_path";
    private String imagePath;
    PhotoView imageView;
    ProgressBar progressBar;

    @Override
    protected void onInitVariable() {
        imagePath = getIntent().getStringExtra(KEY_IMAGE_PATH);
    }

    @Override
    protected void onInitView(Bundle savedInstanceState) {
        this.setContentView(R.layout.comment_image_detail);
        if(TextUtils.isEmpty(imagePath)){
            finish();
        }
        imageView = findViewById(R.id.image);
        progressBar = findViewById(R.id.loading);
        progressBar.setVisibility(View.VISIBLE);

        ImageLoader.ImageListener listener = new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
                if (null != response.getBitmap()) {
                    imageView.setImageBitmap(response.getBitmap());
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
                imageView.setImageResource(R.drawable.default_image);

            }
        };
        volleyImageLoader.displayImage(imagePath, imageView, FrameConstant.SCREEN_WIDTH, FrameConstant.SCREEN_HEIGHT, ImageURLManager.FIX_WIDTH_TRIM_HEIGHT, listener);

        imageView.setCustomClickListener(new CustomClickListener() {

            @Override
            public void onEvent() {
                finish();
            }
        });
    }

    @Override
    protected void onInitEvent() {

    }

    @Override
    protected void onRequestData() {

    }

    @Override
    protected void onLoadData() {

    }

    @Override
    protected void onUnloadData() {

    }

    public static void launch(Context context, String imagePath){
        Intent launcher = new Intent();
        launcher.putExtra(KEY_IMAGE_PATH,imagePath);
        ((BaseActivity)context).startActivity(CommentImageDetailActivity.class, launcher);
    }

}
