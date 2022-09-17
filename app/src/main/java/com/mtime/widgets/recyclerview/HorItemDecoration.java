package com.mtime.widgets.recyclerview;

import android.graphics.Rect;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

/**
 * Created by yinguanping on 2017/5/27.
 */

public class HorItemDecoration extends RecyclerView.ItemDecoration {

    private final int rightPadding;

    public HorItemDecoration(int rightPadding) {
        this.rightPadding = rightPadding;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if (view.getId() == 0) {
            return;
        }
        outRect.right = rightPadding;
    }
}
