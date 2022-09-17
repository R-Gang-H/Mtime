package com.mtime.frame.holder;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kk.taurus.uiframe.v.BaseErrorHolder;
import com.mtime.R;
import com.mtime.base.imageload.ImageHelper;
import com.mtime.base.imageload.ImageProxyUrl;
import com.mtime.base.imageload.ImageShowLoadCallback;
import com.mtime.bussiness.splash.LoadManager;
import com.mtime.constant.FrameConstant;

/**
 * Created by mtime on 2017/10/9.
 */

public class ErrorHolder extends BaseErrorHolder {

    private ImageView mIcon;
    private TextView retryTv;

    public ErrorHolder(Context context) {
        super(context);
    }

    @Override
    public void onCreate() {
        setContentView(R.layout.loading_failed_layout);

        mIcon = getViewById(R.id.load_failed);
        retryTv = getViewById(R.id.retryErrorTv);


        retryTv.setOnClickListener(v -> {
            onHolderEvent(ERROR_EVENT_ON_ERROR_CLICK, null);
        });
    }


    public void setErrorIcon(int resId){
        mIcon.setImageResource(resId);
    }

}
