package com.mtime.bussiness.mine.history.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import androidx.annotation.NonNull;
import androidx.core.view.GestureDetectorCompat;
import androidx.core.view.ViewCompat;
import androidx.customview.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 * 侧滑删除
 *
 * @author ZhouSuQiang
 * @date 2018/8/21
 */
@SuppressLint("ClickableViewAccessibility")
public class SwipeLayout extends ViewGroup {
    private static SwipeLayout sSwipeLayout;
    private final ViewDragHelper dragHelper;
    private OnSwipeChangeListener swipeChangeListener;
    private Status status = Status.CLOSE;//拖拽状态 默认关闭
    private View backView;//侧滑菜单
    private View frontView;//内容区域
    private int height;//自定义控件布局高
    private int width;//自定义控件布局宽
    private int range;//侧滑菜单可滑动范围
    private boolean isSupportSwipe = true;
    private final GestureDetectorCompat mGestureDetector;
    private boolean mNoClick = false;

    public static void closes() {
        if (null != sSwipeLayout) {
            sSwipeLayout.close();
        }
    }

    public static void closes(boolean isSmooth) {
        if (null != sSwipeLayout) {
            sSwipeLayout.close(isSmooth);
        }
    }

    public static boolean isOpen() {
        if (null != sSwipeLayout) {
            return sSwipeLayout.getStatus() == Status.OPEN;
        }
        return false;
    }

    public void setSupportSwipe(boolean supportSwipe) {
        isSupportSwipe = supportSwipe;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public enum Status {
        OPEN, CLOSE, DRAGING
    }

    //拖拽事件监听器
    public interface OnSwipeChangeListener {
        void onDraging(SwipeLayout mSwipeLayout);

        void onOpen(SwipeLayout mSwipeLayout);

        void onClose(SwipeLayout mSwipeLayout);

        void onStartOpen(SwipeLayout mSwipeLayout);

        void onStartClose(SwipeLayout mSwipeLayout);

    }

    public static abstract class SimpleListenerAdapter implements OnSwipeChangeListener {

        @Override
        public void onDraging(SwipeLayout mSwipeLayout) {
        }

        @Override
        public void onOpen(SwipeLayout mSwipeLayout) {
        }

        @Override
        public void onClose(SwipeLayout mSwipeLayout) {
        }

        @Override
        public void onStartOpen(SwipeLayout mSwipeLayout) {
        }

        @Override
        public void onStartClose(SwipeLayout mSwipeLayout) {
        }
    }

    //重写三个构造方法
    public SwipeLayout(Context context) {
        this(context, null);
    }

    public SwipeLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwipeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        dragHelper = ViewDragHelper.create(this, callback);

        mGestureDetector = new GestureDetectorCompat(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return !mNoClick;
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if (mNoClick) { // 当滑动或者展开的时候，不响应点击事件
                    return false;
                }
                return performClick();
            }

        });
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (sSwipeLayout != null && sSwipeLayout == this) {
            sSwipeLayout.close();
            sSwipeLayout = null;
        }
    }

    private final ViewDragHelper.Callback callback = new ViewDragHelper.Callback() {

        //所有子View都可拖拽
        @Override
        public boolean tryCaptureView(@NonNull View child, int pointerId) {
            return true;
        }

        //水平拖拽后处理:移动的边界进行控制
        //决定拖拽的View在水平方向上面移动到的位置
        @Override
        public int clampViewPositionHorizontal(@NonNull View child, int left, int dx) {
            if (child == frontView) {
                if (left > 0) {
                    return 0;
                } else if (left < -range) {
                    return -range; //边界不能超过-range
                }
            } else if (child == backView) {
                if (left > width) {
                    return width;
                } else if (left < width - range) {
                    return width - range;
                }
            }
            return left;
        }

        @Override
        public void onViewPositionChanged(@NonNull View changedView, int left, int top, int dx, int dy) {
            ViewCompat.offsetLeftAndRight(frontView == changedView ? backView : frontView, dx);
            //事件派发
            dispatchSwipeEvent();
        }

        //松手后根据侧滑位移确定菜单打开与否
        @Override
        public void onViewReleased(@NonNull View releasedChild, float xvel, float yvel) {
            if (xvel == 0 && frontView.getLeft() < -range * 0.5f) {
                open();
            } else if (xvel < 0) {
                open();
            } else {
                close();
            }
        }

        //子View如果是clickable，必须重写的方法
        @Override
        public int getViewHorizontalDragRange(@NonNull View child) {
            return 1;
        }

        @Override
        public int getViewVerticalDragRange(@NonNull View child) {
            return 1;
        }

    };

    //根据当前状态判断回调事件
    protected void dispatchSwipeEvent() {
        Status preStatus = status;
        status = updateStatus();

        mNoClick = true;
        if (swipeChangeListener != null) {
            swipeChangeListener.onDraging(this);
        }

        if (preStatus != status) {
            if (status == Status.CLOSE) {
                mNoClick = false;
                if (swipeChangeListener != null) {
                    swipeChangeListener.onClose(this);
                }
            } else if (status == Status.OPEN) {
                mNoClick = true;
                if (swipeChangeListener != null) {
                    swipeChangeListener.onOpen(this);
                }
            } else if (status == Status.DRAGING) {
                mNoClick = true;
                if (sSwipeLayout != this) {
                    closes();
                    sSwipeLayout = this;
                }

                if (preStatus == Status.CLOSE) {
                    if (swipeChangeListener != null) {
                        swipeChangeListener.onStartOpen(this);
                    }
                } else if (preStatus == Status.OPEN) {
                    if (swipeChangeListener != null) {
                        swipeChangeListener.onStartClose(this);
                    }
                }
            }
        }
    }

    //更改状态
    private Status updateStatus() {
        int left = frontView.getLeft();
        if (left == 0) {
            return Status.CLOSE;
        } else if (left == -range) {
            return Status.OPEN;
        }
        return Status.DRAGING;
    }

    // 持续平滑动画 高频调用
    @Override
    public void computeScroll() {
        // 如果返回true，动画还需要继续
        if (dragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    public void open() {
        open(true);
    }

    public void open(boolean isSmooth) {
        if (sSwipeLayout != this) {
            closes();
            sSwipeLayout = this;
        }

        int finalLeft = -range;
        slide(isSmooth, finalLeft);
    }

    public void close() {
        close(true);
    }

    public void close(boolean isSmooth) {
        slide(isSmooth, 0);
    }

    private void slide(boolean isSmooth, int dx) {
        if (isSmooth) {
            if (dragHelper.smoothSlideViewTo(frontView, dx, 0)) {
                ViewCompat.postInvalidateOnAnimation(this);
            }
        } else {
            layoutContent(dx != 0);
        }
    }

    /**
     * @param isOpen 侧滑菜单是否打开
     */
    private void layoutContent(boolean isOpen) {
        Rect frontRect = computeFrontViewRect(isOpen);
        frontView.layout(frontRect.left, frontRect.top, frontRect.right, frontRect.bottom);

        Rect backRect = computeBackViewRect(frontRect);
        backView.layout(backRect.left, backRect.top, backRect.right, backRect.bottom);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measureChildren(widthMeasureSpec, heightMeasureSpec);

        int width = MeasureSpec.getSize(widthMeasureSpec);
        View child = getChildAt(0);
        int margin =
                ((MarginLayoutParams) child.getLayoutParams()).topMargin +
                        ((MarginLayoutParams) child.getLayoutParams()).bottomMargin;
        int padding = getPaddingTop() + getPaddingBottom();

        int height = child.getMeasuredHeight();

        int N = getChildCount();
        for (int i = 1; i < N; ++i) {
            child = getChildAt(i);
            if (child.getVisibility() == GONE) {
                continue;
            }
            LayoutParams lp = child.getLayoutParams();
            int childWidthMeasureSpec = getChildMeasureSpec(widthMeasureSpec,
                    getPaddingLeft() + getPaddingRight(), lp.width);
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
            child.measure(childWidthMeasureSpec, heightMeasureSpec);
        }

        setMeasuredDimension(width, height + margin + padding);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int cCount = getChildCount();
        int lastX = 0;
        for (int i = 0; i < cCount; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == GONE) {
                continue;
            }
            MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();

            if (i == 0) {
                int left = getPaddingLeft() + lp.leftMargin;
                int top = getPaddingTop() + lp.topMargin;
                child.layout(left, top,
                        left + child.getMeasuredWidth(), top + child.getMeasuredHeight());
                lastX = left + child.getMeasuredWidth() + lp.rightMargin;
            } else {
                int left = lastX + lp.leftMargin;
                int top = getPaddingTop() + lp.topMargin;
                child.layout(left, top,
                        left + child.getMeasuredWidth(), top + child.getMeasuredHeight());
                lastX = left + child.getMeasuredWidth() + lp.rightMargin;
            }
        }
    }

    /**
     * 通过内容区域所占矩形坐标计算侧滑菜单的矩形位置区域
     *
     * @param frontRect 内容区域所占矩形
     */
    private Rect computeBackViewRect(Rect frontRect) {
        int left = frontRect.right;
        return new Rect(left, 0, left + range, height);
    }

    /**
     * 通过菜单打开与否isOpen计算内容区域的矩形区
     */
    private Rect computeFrontViewRect(boolean isOpen) {
        int left = 0;
        if (isOpen) {
            left = -range;
        }
        return new Rect(left, 0, left + width, height);
    }

    //获取两个View
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        int childCount = getChildCount();
        if (childCount < 2) {
            throw new IllegalStateException("you need 2 children view");
        }

        frontView = getChildAt(0);//内容区域
        backView = getChildAt(1);//侧滑菜单

    }

    //初始化布局的高height宽width以及可滑动的范围range
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        height = frontView.getMeasuredHeight();
        width = frontView.getMeasuredWidth();
        range = backView.getMeasuredWidth();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (isSupportSwipe) {
            boolean intercept = dragHelper.shouldInterceptTouchEvent(ev);
            getParent().requestDisallowInterceptTouchEvent(intercept);
            intercept = intercept || super.onInterceptTouchEvent(ev);
            return intercept;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isSupportSwipe) {
            mGestureDetector.onTouchEvent(event);
            dragHelper.processTouchEvent(event);
            return true;
        } else {
            return mGestureDetector.onTouchEvent(event);
        }
    }

    public void setOnSwipeChangeListener(OnSwipeChangeListener swipeChangeListener) {
        this.swipeChangeListener = swipeChangeListener;
    }

}

