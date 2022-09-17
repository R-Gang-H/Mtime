package com.mtime.bussiness.ticket.movie.widget;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 重写RecyclerView的滑动事件，监听 向上活向下滑动，触发隐藏和显示头部标题栏和底部栏的事件，以及判断是否到达底部
 * Created by yinguanping on 16/4/1.
 */
public abstract class MyRecyclerViewScrollListener extends RecyclerView.OnScrollListener {

    private static final int HIDE_THRESHOLD = 20;

    private int mScrolledDistance = 0;
    private boolean mControlsVisible = true;


    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        onInterceptScrolled(recyclerView, dx, dy);

//        int firstVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();

//        if (firstVisibleItem == 0) {
//            if (!mControlsVisible) {
//                onShow();
//                mControlsVisible = true;
//            }
//        } else {
        if (mScrolledDistance > HIDE_THRESHOLD && mControlsVisible) {
            onHide();
            mControlsVisible = false;
            mScrolledDistance = 0;
        } else if (mScrolledDistance < -HIDE_THRESHOLD && !mControlsVisible) {
            onShow();
            mControlsVisible = true;
            mScrolledDistance = 0;
        }
//        }
        if ((mControlsVisible && dy > 0) || (!mControlsVisible && dy < 0)) {
            mScrolledDistance += dy;
        }
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        if (!(recyclerView.getLayoutManager() instanceof LinearLayoutManager)) {
            return;
        }
        LinearLayoutManager lm = (LinearLayoutManager) recyclerView.getLayoutManager();
        int visibleItemCount = lm.getChildCount();
        int totalItemCount = lm.getItemCount();
        if ((visibleItemCount > 0 && newState == RecyclerView.SCROLL_STATE_IDLE &&
                (lm.findLastVisibleItemPosition()) >= totalItemCount - 1)) {
            onBottom();
        }
    }

    public abstract void onHide();

    public abstract void onShow();

    public abstract void onInterceptScrolled(RecyclerView recyclerView, int dx, int dy);

    public abstract void onBottom();
}
