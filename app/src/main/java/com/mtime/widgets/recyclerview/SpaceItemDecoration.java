package com.mtime.widgets.recyclerview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 使用ItemDecoration为RecyclerView设置item间距
 * Created by yinguanping on 16/4/5.
 */
public class SpaceItemDecoration extends RecyclerView.ItemDecoration {

    private final int space;
    private final int colorId;

    public SpaceItemDecoration(Context context, int space, int colorId) {
        this.space = space;
        this.colorId = colorId;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent) {
        super.onDraw(c, parent);
        c.drawColor(colorId);
    }

    @Override
    public void getItemOffsets(Rect outRect, int itemPosition, RecyclerView parent) {
        if (itemPosition > 1)
            outRect.top = space;
    }
}
