package com.mtime.bussiness.daily.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.NestedScrollingChild;
import androidx.core.view.NestedScrollingParent;
import androidx.core.view.NestedScrollingParentHelper;
import androidx.core.view.ViewCompat;
import android.util.AttributeSet;
import android.view.Choreographer;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

import com.mtime.R;

/**
 * Created by <a href="mailto:wangkunlin1992@gmail.com">Wang kunlin</a>
 * <p>
 * On 2018-06-14
 */
public class RecommendLayout extends ConstraintLayout implements NestedScrollingParent {

    private NestedScrollingParentHelper mNestedHelper;
    private View mContent;
    private Scroller mScroller;
    private int mScrollMax;
    private Choreographer mGrapher;
    private int mContentOriginLeft;
    private int mContentCurrLeft;
    private float mContentScrollFraction = 2f / 3;

    public RecommendLayout(Context context) {
        super(context);
        init(context, null);
    }

    public RecommendLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public RecommendLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RecommendLayout);
        // 滑动打开的百分比
        mContentScrollFraction = a.getFraction(R.styleable.RecommendLayout_contentScroll,
                1, 1, mContentScrollFraction);
        constraintFraction();
        a.recycle();
        // 嵌套滚动帮助类
        mNestedHelper = new NestedScrollingParentHelper(this);
        mScroller = new Scroller(context);
        // 动画刷新类
        mGrapher = Choreographer.getInstance();
    }

    private void constraintFraction() {
        if (mContentScrollFraction > 1) {
            mContentScrollFraction = 1;
        }
        if (mContentScrollFraction <= 0) {
            mContentScrollFraction = 1f / 3;
        }
    }

    public boolean isClosed() {
        return getContentScroll() == 0;
    }

    @Override
    public boolean onStartNestedScroll(@NonNull View child, @NonNull View target, int nestedScrollAxes) {
        // 嵌套滚动由 子 view 发起，然后回调到父 view
        // 这里 只接受 水平方向的滚动
        stopAnimation();
        return (nestedScrollAxes & ViewCompat.SCROLL_AXIS_HORIZONTAL) != 0 && child == mContent && mEnable;
    }

    @Override
    public void onNestedScrollAccepted(@NonNull View child, @NonNull View target, int axes) {
        mNestedHelper.onNestedScrollAccepted(child, target, axes);
    }

    @Override
    public void onNestedPreScroll(@NonNull View target, int dx, int dy, @NonNull int[] consumed) {
        // 先于 子View 进行滚动，消费一定的距离 dx > 0 向左滚动
        if (mContentOriginLeft != mContentCurrLeft && dx > 0) {
            int remain = getContentScroll();
            int scroll;
            if (remain - dx >= 0) { // 完全消费
                scroll = dx;
                consumed[0] = dx;
            } else { // 部分消费
                scroll = remain;
                consumed[0] = remain;
            }
            moveContent(-scroll);
        }
    }

    private boolean mEnable;

    public void setRecommendEnable(boolean enable) {
        mEnable = enable;
    }

    private int getContentScroll() {
        return mContentCurrLeft - mContentOriginLeft;
    }

    @Override
    public void onNestedScroll(@NonNull View target, int dxConsumed,
                               int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        // 后于 子 View 进行滚动
        handleScroll(dxUnconsumed);
    }

    private void handleScroll(int distanceX) {
        if (distanceX < 0) { // 向右滑动
            int remain = mScrollMax - getContentScroll();
            if (remain == 0) return;
            int absDx = -distanceX;
            int scroll;
            // 同上
            if (remain - absDx >= 0) {
                scroll = absDx;
            } else {
                scroll = remain;
            }
            moveContent(scroll);
        } else { // 向左滑动
            if (getContentScroll() == 0) {
                return;
            }
            int remain = getContentScroll();
            int scroll;
            if (remain - distanceX >= 0) {
                scroll = distanceX;
            } else {
                scroll = remain;
            }
            moveContent(-scroll);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // 计算 打开的实际距离
        mScrollMax = (int) (w * mContentScrollFraction);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        // 寻找 被 滚动的 content
        int N = getChildCount();
        for (int i = 0; i < N; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == GONE) {
                continue;
            }
            LayoutParams params = (LayoutParams) child.getLayoutParams();
            if (params.mIsContent) {
                if (mContent != null && mContent != child) {
                    throw new IllegalStateException("content can only one");
                }
                mContent = child;
            }
        }
//        if (mContent == null) {
//            throw new IllegalStateException("need a content");
//        }
        checkContent();
        mContentOriginLeft = mContent == null ? 0 : mContent.getLeft();
        mContentCurrLeft = mContentOriginLeft;
    }

    private void checkContent() {
        if (mContent != null && Build.VERSION.SDK_INT < 21) {
            if (mContent instanceof NestedScrollingChild) { // 需要能够触发嵌套滚动
                return;
            }
            throw new IllegalStateException("content must implement NestedScrollingChild");
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopAnimation();
    }

    private void moveContent(int scroll) {
        // 移动 内容 mContent.offsetLeftAndRight(scroll);
        ViewCompat.offsetLeftAndRight(mContent, scroll);
        mContentCurrLeft = mContent.getLeft();
        int contentScroll = getContentScroll();
        if (contentScroll == 0) {
            stopAnimation();
        }
        if (mPercentCallback != null) { // 回调 打开的百分比
            float percent = contentScroll * 1f / mScrollMax;
            mPercentCallback.openPercent(percent);
        }
    }

    @Override
    public boolean onNestedPreFling(@NonNull View target, float velocityX, float velocityY) {
        // 嵌套滚动的 fling 内容打开的时候 拦截 fling
        return mEnable && getContentScroll() != 0;
    }

    @Override
    public boolean onNestedFling(@NonNull View target, float velocityX, float velocityY, boolean consumed) {
        return mEnable && getContentScroll() != 0;
    }

    @Override
    public int getNestedScrollAxes() {
        return mNestedHelper.getNestedScrollAxes();
    }

    @Override
    public void onStopNestedScroll(@NonNull View child) {
        mNestedHelper.onStopNestedScroll(child);
        startAnimation();
    }

    private void stopAnimation() {
        mScroller.forceFinished(true);
    }

    private void startAnimation() { // 开始 自动滚动
        stopAnimation();
        int distance = -getContentScroll();
        mScroller.startScroll(mContentCurrLeft, 0, distance, 0);
        // 开启动画
        mGrapher.postFrameCallback(getScrollAnimation());
    }

    private Choreographer.FrameCallback mScrollAnimation;

    private Choreographer.FrameCallback getScrollAnimation() {
        if (mScrollAnimation != null) {
            return mScrollAnimation;
        }
        mScrollAnimation = new Choreographer.FrameCallback() {
            @Override
            public void doFrame(long frameTimeNanos) {
                if (mScroller.computeScrollOffset()) {
                    int currX = mScroller.getCurrX();
                    int distance = currX - getContentScroll();
                    moveContent(distance);
                    mGrapher.postFrameCallback(this);
                }
            }
        };
        return mScrollAnimation;
    }

    // ----- 以下 自定义 LayoutParams
    @Override
    protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams lp) {
        if (lp instanceof LayoutParams) {
            return new LayoutParams((LayoutParams) lp);
        }
        return new LayoutParams(lp);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    public static class LayoutParams extends ConstraintLayout.LayoutParams {

        // 标记
        private boolean mIsContent = false;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            TypedArray a = c.obtainStyledAttributes(attrs, R.styleable.RecommendLayout_Layout);
            mIsContent = a.getBoolean(R.styleable.RecommendLayout_Layout_layout_recommendContent,
                    false);
            a.recycle();
        }

        public LayoutParams(int w, int h) {
            super(w, h);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }

        public LayoutParams(LayoutParams source) {
            super(source);
            this.mIsContent = source.mIsContent;
        }
    }
    // ----- 以上 自定义 LayoutParams

    public interface OpenPercentCallback {
        void openPercent(float percent);
    }

    private OpenPercentCallback mPercentCallback;

    public void setOpenPercentCallback(OpenPercentCallback percentCallback) {
        mPercentCallback = percentCallback;
    }
}
