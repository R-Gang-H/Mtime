package com.aspsine.irecyclerview;

import androidx.recyclerview.widget.RecyclerView;

import android.view.View;

/**
 * Created by aspsine on 16/3/13.
 */
public abstract class OnLoadMoreScrollListener extends RecyclerView.OnScrollListener {

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        int visibleItemCount = layoutManager.getChildCount();
        int preloadCount = getPreloadCount();

        boolean triggerCondition = visibleItemCount > 0
                && newState == RecyclerView.SCROLL_STATE_IDLE
                && canTriggerLoadMore(recyclerView, preloadCount);
        
        if (triggerCondition) {
            onLoadMore(recyclerView);
        }
    }

    private boolean canTriggerLoadMore(RecyclerView recyclerView, int preloadCount) {
        View lastChild = recyclerView.getChildAt(recyclerView.getChildCount() - 1);
        int position = recyclerView.getChildLayoutPosition(lastChild);
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        int totalItemCount = layoutManager.getItemCount();
        return position >= totalItemCount - 1 - preloadCount;
    }

    public abstract void onLoadMore(RecyclerView recyclerView);
    
    public abstract int getPreloadCount();
}
