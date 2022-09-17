package com.mtime.bussiness.ticket.movie.comment.widget;

import android.content.Context;
import android.graphics.Rect;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

/**
 * Created by <a href="mailto:wangkunlin1992@gmail.com">Wang kunlin</a>
 * <p>
 * On 2019-05-24
 * <p>
 * https://stackoverflow.com/questions/44993103/recyclerview-disable-scrolling-caused-by-change-in-focus
 * https://pastebin.com/8JLSMkF7
 */
public class CommentLayoutManager extends LinearLayoutManager {
    private final Rect parentRect = new Rect();
    private final Rect childRect = new Rect();

    private RecyclerView mParent;
    private boolean mHandleScroll = true;

    public void setHandleScroll(boolean handleScroll) {
        mHandleScroll = handleScroll;
    }

    public CommentLayoutManager(Context context) {
        super(context);
    }

    @Override
    public void onAttachedToWindow(RecyclerView view) {
        super.onAttachedToWindow(view);
        mParent = view;
    }

    @Override
    public View onInterceptFocusSearch(View focused, int direction) {
        if (focused != null) { // 防止焦点乱跑
            focused.getHitRect(childRect);
            mParent.getHitRect(parentRect);
            if (parentRect.contains(childRect)) {
                return focused;
            }
        }
        return super.onInterceptFocusSearch(focused, direction);
    }

    /**
     * 防止 自动滚动
     */
    @Override
    public boolean requestChildRectangleOnScreen(RecyclerView parent, View child, Rect rect, boolean immediate, boolean focusedChildVisible) {
        if (!mHandleScroll) {
            return false;
        }
        parent.getHitRect(parentRect);
        child.getHitRect(childRect);
        if (parentRect.contains(childRect)) {
            return false;
        }
        return super.requestChildRectangleOnScreen(parent, child, rect, immediate, focusedChildVisible);
    }
}
