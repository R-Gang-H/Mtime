package com.mtime.mtmovie.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by guangshun on 15-6-23.
 */
public class MyImageView extends ImageView {
    private OnMeasureListener onMeasureListener;

    public void setOnMeasureListener(OnMeasureListener onMeasureListener) {
        this.onMeasureListener = onMeasureListener;
    }

    public MyImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);

        if (onMeasureListener != null) {
            onMeasureListener.onMeasureSize(getMeasuredWidth(),
                    getMeasuredWidth());
        }
    }

    public interface OnMeasureListener {
        void onMeasureSize(int width, int height);
    }
}
