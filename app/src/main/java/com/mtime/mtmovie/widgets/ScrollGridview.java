package com.mtime.mtmovie.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * Created by guangshun on 15-6-20.
 * 用于处理gridview在ScrollView中显示不全的问题
 */
public class ScrollGridview extends GridView {
    public ScrollGridview(Context context) {
        super(context);
    }
    public ScrollGridview(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScrollGridview(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(
                Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST));
    }
}
