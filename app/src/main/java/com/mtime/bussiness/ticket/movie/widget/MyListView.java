package com.mtime.bussiness.ticket.movie.widget;

import android.widget.ListView;

public class MyListView extends ListView {
    public MyListView(final android.content.Context context, final android.util.AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        final int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);

    }

}
