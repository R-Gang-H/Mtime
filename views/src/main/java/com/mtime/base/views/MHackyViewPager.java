package com.mtime.base.views;


import android.content.Context;
import androidx.viewpager.widget.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.mtime.base.utils.MLogWriter;

/**
 * /** Hacky fix for Issue #4 and http://code.google.com/p/android/issues/detail?id=18990
 * <p>
 * ScaleGestureDetector seems to mess up the touch events, which means that ViewGroups which make use of
 * onInterceptTouchEvent throw a lot of IllegalArgumentException: pointerIndex out of range.
 * <p>
 * There's not much I can do in my code for now, but we can mask the result by just catching the problem and ignoring
 * it.
 * Created by LiJiaZhi on 16/11/25.
 */

public class MHackyViewPager extends ViewPager {
    private static final String TAG = "LPHackyViewPager";
    private boolean isLocked = false;

    public MHackyViewPager(Context context) {
        super(context);
    }

    public MHackyViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setLocked(boolean isLocked) {
        this.isLocked = isLocked;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!isLocked) {
            try {
                return super.onInterceptTouchEvent(ev);
            } catch (IllegalArgumentException e) {
                // 不理会
                MLogWriter.e(TAG, "hacky viewpager error1");
                return false;
            } catch (ArrayIndexOutOfBoundsException e) {
                // 不理会
                MLogWriter.e(TAG, "hacky viewpager error2");
                return false;
            }
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return !isLocked && super.onTouchEvent(event);
    }
}

