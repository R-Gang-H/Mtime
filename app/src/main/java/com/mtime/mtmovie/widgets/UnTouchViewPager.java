/**
 * 
 */
package com.mtime.mtmovie.widgets;

import android.content.Context;
import androidx.viewpager.widget.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * @author wangjin
 * 
 */
public class UnTouchViewPager extends ViewPager {

    private boolean noScroll = false;

    /**
     * @param context
     */
    public UnTouchViewPager(final Context context) {
        super(context);
    }

    public UnTouchViewPager(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    /*
     * @see androidx.viewpager.widget.ViewPager#onTouchEvent(android.view.MotionEvent)
     */
    @Override
    public boolean onTouchEvent(final MotionEvent arg0) {
        return !noScroll && super.onTouchEvent(arg0);
    }

    /*
     * @see androidx.viewpager.widget.ViewPager#onInterceptTouchEvent(android.view. MotionEvent)
     */
    @Override
    public boolean onInterceptTouchEvent(final MotionEvent arg0) {
        try {
            return !noScroll && super.onInterceptTouchEvent(arg0);
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void setNoScroll(boolean noscroll) {
        this.noScroll = noscroll;
    }

}
