package com.mtime.base.widget.banner;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.List;

/**
 * 无限循环的ViewPager
 */

public class LoopViewPager extends ViewPager {
    private List<OnPageChangeListener> mOuterPageChangeListeners;
    private AbsBannerAdapter mAdapter;
    private PageTransformer mTransformer;
    private boolean mIsTouchScroll = true;
    private boolean mCanLoop = true;

    public LoopViewPager(Context context) {
        super(context);
        init();
    }

    public LoopViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        super.addOnPageChangeListener(onPageChangeListener);
    }

    public void setAdapter(AbsBannerAdapter adapter) {
        mAdapter = adapter;
        mAdapter.setCanLoop(mCanLoop);
        super.setAdapter(mAdapter);

        if (mCanLoop && adapter.getRealCount() > 1) {
            int pos = Integer.MAX_VALUE / 2;
            setCurrentItem(pos - mAdapter.getRealPosition2AdapterPostiton(pos), false);
        }
    }

    public AbsBannerAdapter getAdapter() {
        return mAdapter;
    }

    public boolean isTouchScroll() {
        return mIsTouchScroll;
    }

    public void setTouchScroll(boolean isCanScroll) {
        this.mIsTouchScroll = isCanScroll;
    }

    public int getRealItem(int position) {
        return mAdapter.getRealPosition2AdapterPostiton(position);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mIsTouchScroll) {
            return super.onTouchEvent(ev);
        } else
            return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (mIsTouchScroll)
            return super.onInterceptTouchEvent(ev);
        else
            return false;
    }

    @Override
    public void addOnPageChangeListener(OnPageChangeListener listener) {
        getOuterPageChangeListeners().add(listener);
    }

    List<OnPageChangeListener> getOuterPageChangeListeners() {
        if (mOuterPageChangeListeners == null) {
            mOuterPageChangeListeners = new ArrayList<>();
        }
        return mOuterPageChangeListeners;
    }

    @Override
    public void removeOnPageChangeListener(OnPageChangeListener listener) {
        getOuterPageChangeListeners().remove(listener);
    }

    public boolean isCanLoop() {
        return mCanLoop;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        if (mTransformer != null) {
            final int scrollX = getScrollX();
            final int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                final View child = getChildAt(i);
                final LayoutParams lp = (LayoutParams) child.getLayoutParams();

                if (lp.isDecor) continue;
                final float transformPos = (float) (child.getLeft() - scrollX) / getClientWidth();
                mTransformer.transformPage(child, transformPos);
            }
        }
    }

    public LoopViewPager setCanLoop(boolean canLoop) {
        this.mCanLoop = canLoop;
        if (mAdapter == null)
            return this;
        int position = getCurrentItem();

        mAdapter.setCanLoop(canLoop);
        mAdapter.notifyDataSetChanged();
        setCurrentItem(position, false);
        return this;
    }

    private int getClientWidth() {
        return getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
    }

    @Override
    public void setPageTransformer(boolean reverseDrawingOrder, PageTransformer transformer) {
        this.mTransformer = transformer;
        super.setPageTransformer(reverseDrawingOrder, transformer);
    }

    private final OnPageChangeListener onPageChangeListener = new OnPageChangeListener() {
        private float mPreviousPosition = -1;

        @Override
        public void onPageSelected(int position) {

            int realPosition = getRealItem(position);
            if (mPreviousPosition != realPosition) {
                mPreviousPosition = realPosition;
                for (int i = 0; i < getOuterPageChangeListeners().size(); i++) {
                    getOuterPageChangeListeners().get(i).onPageSelected(realPosition);
                }
            }
        }

        @Override
        public void onPageScrolled(int position, float positionOffset,
                                   int positionOffsetPixels) {
            int realPosition = position;
            realPosition = mAdapter.getRealPosition2AdapterPostiton(realPosition);
            for (int i = 0; i < getOuterPageChangeListeners().size(); i++) {
                getOuterPageChangeListeners().get(i).onPageScrolled(realPosition,
                        positionOffset, positionOffsetPixels);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            for (int i = 0; i < getOuterPageChangeListeners().size(); i++) {
                getOuterPageChangeListeners().get(i).onPageScrollStateChanged(state);
            }
        }
    };
}
