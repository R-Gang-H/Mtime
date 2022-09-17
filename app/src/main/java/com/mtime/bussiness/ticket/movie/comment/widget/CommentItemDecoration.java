package com.mtime.bussiness.ticket.movie.comment.widget;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import com.mtime.base.utils.MScreenUtils;

/**
 * Created by <a href="mailto:wangkunlin1992@gmail.com">Wang kunlin</a>
 * <p>
 * On 2019-05-22
 */
public class CommentItemDecoration extends RecyclerView.ItemDecoration {

    private final Drawable mDrawable;
    private final int mHeight;
    private final int mDp12_5;

    public CommentItemDecoration(Drawable drawable) {
        mDrawable = drawable;
        mHeight = MScreenUtils.dp2px(30);
        mDp12_5 = MScreenUtils.dp2px(12.5f);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        if (position == 1) {
            // 第一个item 的顶部距离第0 个的底部距离 20dp
            outRect.top = mDp12_5;
        }
    }

    private final Rect mRect = new Rect();

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int N = parent.getChildCount();
        for (int i = 0; i < N; ++i) {
            View child = parent.getChildAt(i);
            int position = parent.getChildAdapterPosition(child);
            if (position == 0) { // 在第0个 item 的底部绘制阴影
                Rect r = mRect;
                r.setEmpty();
                parent.getDecoratedBoundsWithMargins(child, r);
                Drawable d = mDrawable;
                d.setBounds(0, r.bottom, r.right, r.bottom + mHeight);
                d.draw(c);
                break;
            }
        }
    }
}
