package com.mtime.bussiness.ticket.cinema.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

/**
 * 解决scrollview里嵌套viewpager，viewpager滑动困难的问题
 * 
 */
public class ScrollViewWithViewPager extends ScrollView {
    // 滑动距离及坐标
    private float xDistance, yDistance, xLast, yLast;
//    private float x, y;
    
    public ScrollViewWithViewPager(Context context) {
        super(context);
    }
    
    public ScrollViewWithViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    
    public ScrollViewWithViewPager(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                xDistance = yDistance = 0f;
                xLast = ev.getX();
                yLast = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                final float curX = ev.getX();
                final float curY = ev.getY();
                
                xDistance += Math.abs(curX - xLast);
                yDistance += Math.abs(curY - yLast);
                xLast = curX;
                yLast = curY;
                
                if (xDistance > yDistance) {
                    return false;
                }
        }

        try {
            return super.onInterceptTouchEvent(ev);
        } catch (Exception e) {

        }

        return false;
    }
    
//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        x = this.getScrollX();
//        y = this.getScrollY();
//        
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//    }
//    
//    @Override
//    protected void onLayout(boolean changed, int l, int t, int r, int b) {
//        
//        int intx = (int)x;
//        int inty = (int)y;
//         
//        super.onLayout(changed, l, t, r, b);
//        
//        this.scrollTo(intx, inty);  
//    }
}
