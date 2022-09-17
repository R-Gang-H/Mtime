package com.mtime.mtmovie.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * Created by guangshun on 15-11-2.
 */
public class MovieRelationGridView extends GridView {
    public MovieRelationGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MovieRelationGridView(Context context) {
        super(context);
    }

    public MovieRelationGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int expandSpec = MeasureSpec.makeMeasureSpec(
                Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
