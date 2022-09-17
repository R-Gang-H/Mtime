package com.mtime.bussiness.video;

import android.content.Context;
import android.graphics.PointF;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;
import android.util.DisplayMetrics;

/**
 * Created by mtime on 2017/12/23.
 */

public class ScrollSpeedLinearLayoutManager extends LinearLayoutManager {

    private float MILLISECONDS_PER_INCH = 0.03f;
    private final Context mContext;
    private OnScrollLayoutListener onScrollLayoutListener;
    private int mTopPadding;

    public void setOnScrollLayoutListener(OnScrollLayoutListener onScrollLayoutListener) {
        this.onScrollLayoutListener = onScrollLayoutListener;
    }

    public ScrollSpeedLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
        this.mContext = context;
    }

    public void setTopPadding(int mTopPadding) {
        this.mTopPadding = mTopPadding;
    }

    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
        LinearSmoothScroller linearSmoothScroller =
                new LinearSmoothScroller(recyclerView.getContext()) {
                    @Override
                    protected void onStart() {
                        super.onStart();
                        if (onScrollLayoutListener != null) {
                            onScrollLayoutListener.onStartScroll();
                        }
                    }

                    @Override
                    protected int getVerticalSnapPreference() {
                        return SNAP_TO_START;
                    }

                    @Override
                    public PointF computeScrollVectorForPosition(int targetPosition) {
                        return ScrollSpeedLinearLayoutManager.this
                                .computeScrollVectorForPosition(targetPosition);
                    }

                    @Override
                    public int calculateDtToFit(int viewStart, int viewEnd, int boxStart, int boxEnd, int snapPreference) {
                        if (snapPreference == SNAP_TO_START) {
                            return boxStart + mTopPadding - viewStart;
                        }
                        return super.calculateDtToFit(viewStart, viewEnd, boxStart, boxEnd, snapPreference);
                    }

                    //This returns the milliseconds it takes to
                    //scroll one pixel.
                    @Override
                    protected float calculateSpeedPerPixel
                    (DisplayMetrics displayMetrics) {
                        return MILLISECONDS_PER_INCH / displayMetrics.density;
                        //返回滑动一个pixel需要多少毫秒
                    }

                    @Override
                    protected void onStop() {
                        super.onStop();
                        if (onScrollLayoutListener != null) {
                            onScrollLayoutListener.onStopScroll();
                        }
                    }
                };
        linearSmoothScroller.setTargetPosition(position);
        startSmoothScroll(linearSmoothScroller);
    }


    public void setSpeedSlow() {
        //自己在这里用density去乘，希望不同分辨率设备上滑动速度相同
        //0.3f是自己估摸的一个值，可以根据不同需求自己修改
        MILLISECONDS_PER_INCH = mContext.getResources().getDisplayMetrics().density * 0.3f;
    }

    public void setSpeedFast() {
        MILLISECONDS_PER_INCH = mContext.getResources().getDisplayMetrics().density * 0.02f;
    }


    public interface OnScrollLayoutListener {
        void onStartScroll();

        void onStopScroll();
    }

}
