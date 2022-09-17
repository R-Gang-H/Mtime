package com.mtime.bussiness.ticket.movie.widget;

import android.content.Context;
import android.util.AttributeSet;
@Deprecated
public class MyWebView extends BaseWebView {
    public MyWebView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        final int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);

    }
}
