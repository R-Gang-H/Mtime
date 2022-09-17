package com.mtime.bussiness.video.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.mtime.base.widget.layout.OnVisibilityListener;

/**
 * Created by Taurus on 2017/11/30.
 */

public class VisibilityStateLinearLayout extends LinearLayout {

    private final String TAG = "StateLayout";

    private OnVisibilityListener onVisibilityListener;

    public VisibilityStateLinearLayout(Context context) {
        this(context, null);
    }

    public VisibilityStateLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setOnVisibilityListener(OnVisibilityListener onVisibilityListener) {
        this.onVisibilityListener = onVisibilityListener;
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        String visibilityText = getVisibilityText(visibility);
        Log.d(TAG,"onWindowVisibilityChanged = " + visibilityText);
        if(onVisibilityListener!=null){
            onVisibilityListener.onVisibilityChange(visibility);
        }
    }

    private String getVisibilityText(int visibility){
        switch (visibility){
            case View.VISIBLE:
                return "VISIBLE";
            case View.INVISIBLE:
                return "INVISIBLE";
            case View.GONE:
                return "GONE";
        }
        return String.valueOf(visibility);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        Log.d(TAG,"onAttachedToWindow");
        if(onVisibilityListener!=null){
            onVisibilityListener.onAttachedToWindow();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Log.d(TAG,"onDetachedFromWindow");
        if(onVisibilityListener!=null){
            onVisibilityListener.onDetachedFromWindow();
        }
    }

}
