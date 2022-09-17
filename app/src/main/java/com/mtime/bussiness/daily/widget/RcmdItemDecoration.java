package com.mtime.bussiness.daily.widget;

import android.graphics.Rect;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

/**
 * Created by <a href="mailto:wangkunlin1992@gmail.com">Wang kunlin</a>
 * <p>
 * On 2018-06-14
 */
public class RcmdItemDecoration extends RecyclerView.ItemDecoration {
    private int mItemOffsets;

    public void setItemOffsets(int itemOffsets) {
        this.mItemOffsets = itemOffsets;
    }

    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.left = mItemOffsets / 2;
        outRect.right = mItemOffsets / 2;
    }
}
