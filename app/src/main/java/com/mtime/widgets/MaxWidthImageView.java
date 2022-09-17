package com.mtime.widgets;

import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.appcompat.widget.AppCompatImageView;
import android.util.AttributeSet;

/**
 * Created by CSY on 2018/7/27.
 * <宽度撑满屏幕的imageView>
 */
public class MaxWidthImageView extends AppCompatImageView {
    public MaxWidthImageView(Context context, AttributeSet attr) {
        super(context, attr);
    }

    public MaxWidthImageView(Context context) {
        super(context);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        Drawable drawable = getDrawable();

        if (null != drawable) {
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int height = width * drawable.getIntrinsicHeight() / drawable.getIntrinsicWidth();
            setMeasuredDimension(width, height);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }
}
