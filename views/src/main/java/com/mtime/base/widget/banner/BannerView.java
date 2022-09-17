package com.mtime.base.widget.banner;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;
import kotlin.Deprecated;

/**
 */

@Deprecated(message = "请使用com.kotlin.android.widget.banner.Banner")
public class BannerView extends FrameLayout {
    private AutoTurnViewPager mViewPager;
    private IBannerIndicator mIndicator;
    private View mIndicatorView;

    public BannerView(@NonNull Context context) {
        this(context, null);
    }

    public BannerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BannerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, @Nullable AttributeSet attrs) {
        mViewPager = new AutoTurnViewPager(context);
        mViewPager.initAttrs(attrs);
        LayoutParams params = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mViewPager.setLayoutParams(params);
        addView(mViewPager);
    }

    public AutoTurnViewPager getViewPager() {
        return mViewPager;
    }

    public BannerView setAdapter(AbsBannerAdapter adapter) {
        stopTurning();
        mViewPager.setAdapter(adapter);
        if (null != mIndicator) {
            mIndicator.onNotifyDataChanged(adapter.getRealCount());
        }
        return this;
    }

    public AbsBannerAdapter getAdapter() {
        return mViewPager.getAdapter();
    }

    /**
     * 设置指示器
     *
     * @param indicator 实现了指示器接口的子类
     * @return
     */
    public BannerView setIndicator(IBannerIndicator indicator) {
        if (null != mIndicatorView) {
            removeView(mIndicatorView);
        }
        mIndicator = indicator;
        mIndicatorView = indicator.onCreateIndicatorView(LayoutInflater.from(getContext()), this);
        if (null != mIndicatorView && null == mIndicatorView.getParent()) {
            setIndicatorGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
            addView(mIndicatorView);
        }
        if (null != mViewPager.getAdapter()) {
            mIndicator.onNotifyDataChanged(mViewPager.getAdapter().getRealCount());
        }
        mViewPager.addOnPageChangeListener(indicator);
        return this;
    }

    /**
     * 设置指示器的位置
     *
     * @param gravity {@code Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL}
     */
    public BannerView setIndicatorGravity(int gravity) {
        if (null != mIndicatorView) {
            LayoutParams params = (LayoutParams) mIndicatorView.getLayoutParams();
            if (null == params) {
                params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            }
            params.gravity = gravity;
            mIndicatorView.setLayoutParams(params);
        }
        return this;
    }

    /**
     * 设置指示器是否可见
     */
    public BannerView setIndicatorVisible(boolean visible) {
        if (null != mIndicatorView)
            mIndicatorView.setVisibility(visible ? View.VISIBLE : View.GONE);
        return this;
    }

    public BannerView setCurrentItem(int page) {
        stopTurning();
        mViewPager.setCurrentItem(page);
        startTurn();
        return this;
    }

    public BannerView startTurn() {
        mViewPager.startTurn();
        return this;
    }

    public BannerView stopTurning() {
        mViewPager.stopTurning();
        return this;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mViewPager.isCanTurn())
            startTurn();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopTurning();
    }

    /**
     * 自定义翻页动画效果
     */
    public BannerView setPageTransformer(ViewPager.PageTransformer transformer) {
        mViewPager.setPageTransformer(true, transformer);
        return this;
    }

    public BannerView setScrollDuration(int duration) {
        mViewPager.setScrollDuration(duration);
        return this;
    }

    public BannerView setCanLoop(boolean canLoop) {
        mViewPager.setCanLoop(canLoop);
        return this;
    }

    public BannerView setCanTurn(boolean canTurn) {
        mViewPager.setCanTurn(canTurn);
        return this;
    }

    public BannerView setAutoTurnTime(int autoTurnTime) {
        mViewPager.setAutoTurnTime(autoTurnTime);
        return this;
    }

    public BannerView setReverse(boolean reverse) {
        mViewPager.setReverse(reverse);
        return this;
    }

    public BannerView setTouchScroll(boolean isCanScroll) {
        mViewPager.setTouchScroll(isCanScroll);
        return this;
    }

    public int getCurrentItem() {
        return mViewPager.getCurrentItem();
    }

    public int getScrollDuration() {
        return mViewPager.getScrollDuration();
    }

    public int getAutoTurnTime() {
        return mViewPager.getAutoTurnTime();
    }

    public boolean isReverse() {
        return mViewPager.isReverse();
    }

    public boolean isCanTurn() {
        return mViewPager.isCanTurn();
    }

    public boolean isRunning() {
        return mViewPager.isRunning();
    }

    public boolean isCanLoop() {
        return mViewPager.isCanLoop();
    }

    public boolean isTouchScroll() {
        return mViewPager.isTouchScroll();
    }
}
