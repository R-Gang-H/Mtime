package com.mtime.bussiness.video.view;

import android.content.Context;
import androidx.viewpager.widget.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by JiaJunHui on 2018/3/30.
 */

public class ScrollSettingViewPager extends ViewPager {

    private boolean scrollEnable = true;

    public ScrollSettingViewPager(Context context) {
        super(context);
    }

    public ScrollSettingViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setScrollEnable(boolean enable){
        this.scrollEnable = enable;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if(!scrollEnable)
            return false;
        return super.onTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if(!scrollEnable)
            return false;
        return super.onInterceptTouchEvent(ev);
    }
}
