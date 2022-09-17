package com.mtime.base.widget.banner;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.viewpager.widget.ViewPager;

import com.mtime.base.views.R;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;

/**
 * 自动翻页ViewPager
 */

public class AutoTurnViewPager extends LoopViewPager {
    private boolean mIsRunning; //是否正在执行翻页中   如果是canLoop为true 到头了 那就不翻页
    private boolean mCanTurn;   //能否能执行自动翻页
    private int mAutoTurnTime = 5000; //翻页间隔时间
    private boolean mReverse; //倒转翻页

    private ViewPagerScroller mPagerScroller;
    public TurnRunnable mTurnRunnable;

    public AutoTurnViewPager(Context context) {
        this(context, null);
    }

    public AutoTurnViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        mTurnRunnable = new TurnRunnable(this);
        setRunning(true);
        setCanTurn(true);
        initViewPagerScroll();
        initAttrs(attrs);
    }

    void initAttrs(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.AutoTurnViewPager);
        if (a != null) {
            int n = a.getIndexCount();
            for (int i = 0; i < n; i++) {
                int attr = a.getIndex(i);
                if (attr == R.styleable.AutoTurnViewPager_bv_canLoop) {
                    setCanLoop(a.getBoolean(attr, true));
                } else if (attr == R.styleable.AutoTurnViewPager_bv_canTurn) {
                    setCanTurn(a.getBoolean(attr, true));
                } else if (attr == R.styleable.AutoTurnViewPager_bv_isTouchScroll) {
                    setTouchScroll(a.getBoolean(attr, true));
                } else if (attr == R.styleable.AutoTurnViewPager_bv_autoTurnTime) {
                    setAutoTurnTime(a.getInteger(attr, getAutoTurnTime()));
                } else if (attr == R.styleable.AutoTurnViewPager_bv_scrollDuration) {
                    setScrollDuration(a.getInteger(attr, getScrollDuration()));
                } else if (attr == R.styleable.AutoTurnViewPager_bv_reverse) {
                    setReverse(a.getBoolean(attr, false));
                }
            }
            a.recycle();
        }
    }

    /**
     * 设置ViewPager的滑动速度
     */
    private void initViewPagerScroll() {
        try {
            Field mScroller = null;
            mScroller = ViewPager.class.getDeclaredField("mScroller");
            mScroller.setAccessible(true);
            this.mPagerScroller = new ViewPagerScroller(
                    getContext());
            mScroller.set(this, this.mPagerScroller);

        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        startTurn();
    }

    @Override
    protected void onDetachedFromWindow() {
        stopTurning();
        super.onDetachedFromWindow();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_OUTSIDE) {
            startTurn();
        } else if (action == MotionEvent.ACTION_DOWN) {
            stopTurning();
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 自定义翻页动画效果
     */
    public AutoTurnViewPager setPageTransformer(PageTransformer transformer) {
        setPageTransformer(true, transformer);
        return this;
    }

    private AutoTurnViewPager startTurn(int autoTurnTime) {
        stopTurning();
        setRunning(true);
        setAutoTurnTime(autoTurnTime);
        postDelayed(mTurnRunnable, this.mAutoTurnTime);
        return this;
    }

    public AutoTurnViewPager startTurn() {
        startTurn(mAutoTurnTime);
        return this;
    }

    public AutoTurnViewPager stopTurning() {
        //关闭翻页
        setRunning(false);
        removeCallbacks(mTurnRunnable);
        return this;
    }

    private void setRunning(boolean running) {
        mIsRunning = running;
    }

    public AutoTurnViewPager setCanTurn(boolean canTurn) {
        if (this.mCanTurn == canTurn)
            return this;
        this.mCanTurn = canTurn;
        if (canTurn) {
            startTurn();
        } else {
            stopTurning();
        }
        return this;
    }

    @Override
    public AutoTurnViewPager setCanLoop(boolean canLoop) {
        if (!isRunning()) {
            startTurn();
        }
        super.setCanLoop(canLoop);
        return this;
    }

    public AutoTurnViewPager setAutoTurnTime(int autoTurnTime) {
        this.mAutoTurnTime = autoTurnTime;
        return this;
    }

    public AutoTurnViewPager setScrollDuration(int duration) {
        mPagerScroller.setScrollDuration(duration);
        return this;
    }

    public AutoTurnViewPager setReverse(boolean reverse) {
        this.mReverse = reverse;
        return this;
    }

    public boolean isRunning() {
        return mIsRunning;
    }

    public boolean isCanTurn() {
        return mCanTurn;
    }

    public int getAutoTurnTime() {
        return mAutoTurnTime;
    }

    public int getScrollDuration() {
        return mPagerScroller.getScrollDuration();
    }

    public boolean isReverse() {
        return mReverse;
    }


    static class TurnRunnable implements Runnable {

        private final WeakReference<AutoTurnViewPager> reference;

        TurnRunnable(AutoTurnViewPager autoTurnViewPager) {
            this.reference = new WeakReference(autoTurnViewPager);
        }

        @Override
        public void run() {
            // 开始翻页
            AutoTurnViewPager autoTurnViewPager = reference.get();

            if (autoTurnViewPager != null && null != autoTurnViewPager.getAdapter() && autoTurnViewPager.getAdapter().getCount() > 0) {
                if (autoTurnViewPager.isRunning() && autoTurnViewPager.isCanTurn()) {
                    final int page = autoTurnViewPager.getCurrentItem() + (autoTurnViewPager.isReverse() ? -1 : 1);
                    if (autoTurnViewPager.getAdapter().getCount() <= page) {
                        autoTurnViewPager.setRunning(false);
                        return;
                    }
                    autoTurnViewPager.setCurrentItem(page, true);
                    autoTurnViewPager.startTurn();
                }
            } else if (null != autoTurnViewPager){
                autoTurnViewPager.setRunning(false);
            }
        }
    }
}
