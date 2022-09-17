package com.mtime.mtmovie.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

import com.mtime.R;

public class EventScrollView extends ScrollView {
    public enum ScrollViewEventType {
        TYPE_EXIT, TYPE_ALPHA_CHANGED, TYPE_BOTTOM_VISIBILE_CHANGED, TYPE_LAYOUT_CHANGED, TYPE_SCROLL_TO_BOTTOM, TYPE_SCROLL_CHANGED
    }
    
    public interface IEventScrollViewListener {
        void onEvent(ScrollViewEventType type, Object value);
    }
    
    private final int                DISTANCEX             = 80;
    private final int                DISTANCEY             = 60;
    
    private int                      startX                = 0;
    private int                      startY                = 0;
    
    private int                      screenHeight;
    private int                      titleHeight;
    private int                      excludeHeightOfBottom = 1280;
    private boolean                  isFling;
    
    private boolean                  reachBottom = false;
    private boolean                  isDown = false;
    
    private IEventScrollViewListener listener;
    
    public EventScrollView(Context context) {
        super(context);
    }
    
    public EventScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    
    @Override
    public void fling(int velocityY) {
        isFling = true;
        super.fling(velocityY);
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        
        final int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                isFling = false;
                if (0 == startX) {
                    startX = (int) ev.getX();
                    startY = (int) ev.getY();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (0 == startX) {
                    startX = (int) ev.getX();
                    startY = (int) ev.getY();
                }

                isDown = reachBottom;
                
                // change current title alpha
                if (null != listener) {
                    float alpha = 1.0f * getScrollY() / titleHeight;
                    float value = titleHeight > getScrollY() ? alpha : 1.0f;
                    
                    listener.onEvent(ScrollViewEventType.TYPE_ALPHA_CHANGED, value);
                    
                    if (this.getScrollY() + screenHeight > excludeHeightOfBottom) {
                        listener.onEvent(ScrollViewEventType.TYPE_BOTTOM_VISIBILE_CHANGED, true);
                    }
                    else {
                        listener.onEvent(ScrollViewEventType.TYPE_BOTTOM_VISIBILE_CHANGED, false);
                    }

                    listener.onEvent(ScrollViewEventType.TYPE_SCROLL_CHANGED, getScrollY());
                }
                
                break;
            case MotionEvent.ACTION_UP:
                final int endX = (int) ev.getX();
                final int endY = (int) ev.getY();
                
                // change current title alpha
                if (null != listener) {
                    // alpha changed.
                    float alpha = 1.0f * getScrollY() / titleHeight;
                    float value = titleHeight > getScrollY() ? alpha : 1.0f;
                    listener.onEvent(ScrollViewEventType.TYPE_ALPHA_CHANGED, value);
                    
                    // exit current activity
                    if (DISTANCEX < (endX - startX) && DISTANCEY > Math.abs(endY - startY)) {
                        listener.onEvent(ScrollViewEventType.TYPE_EXIT, null);
                    }
                    
                    // show/hide bottom view
                    if (this.getScrollY() + screenHeight > excludeHeightOfBottom) {
                        listener.onEvent(ScrollViewEventType.TYPE_BOTTOM_VISIBILE_CHANGED, true);
                        
                    }
                    else {
                        listener.onEvent(ScrollViewEventType.TYPE_BOTTOM_VISIBILE_CHANGED, false);
                    }
                    
                    //
                    int pos = this.getScrollY();
                    boolean reachAgain = (pos + this.getHeight()) >= (getChildAt(0).getMeasuredHeight() - 70);
                    if (!reachAgain) {
                        reachBottom = false;
                    }
                    if (isDown && reachBottom && reachAgain) {
                        listener.onEvent(ScrollViewEventType.TYPE_SCROLL_TO_BOTTOM, null);
                    }

                    listener.onEvent(ScrollViewEventType.TYPE_SCROLL_CHANGED, pos);
                }
                
                startX = 0;
                startY = 0;
                break;
            default:
                break;
        }
        
        return super.onTouchEvent(ev);
    }
    
    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {

        int pos = this.getScrollY();
        if (isFling && null != listener) {
            float alpha = 0;
            if (pos < titleHeight && pos > 0) {
                alpha = 1.0f * t / titleHeight;
            }
            if (pos >= titleHeight) {
                alpha = 1;
            }
            // title alpha
            listener.onEvent(ScrollViewEventType.TYPE_ALPHA_CHANGED, alpha);
            // bottom view show/hide
            if (pos + screenHeight > excludeHeightOfBottom) {
                listener.onEvent(ScrollViewEventType.TYPE_BOTTOM_VISIBILE_CHANGED, true);
            }
            else {
                listener.onEvent(ScrollViewEventType.TYPE_BOTTOM_VISIBILE_CHANGED, false);
            }
            if ((pos + this.getHeight()) >= (getChildAt(0).getMeasuredHeight() - 70)) {
                reachBottom = true;
            }
            
        }
        
        if (null != listener) {
            listener.onEvent(ScrollViewEventType.TYPE_SCROLL_CHANGED, pos);
        }
       
        super.onScrollChanged(l, t, oldl, oldt);
    }
    
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (null != listener) {
            listener.onEvent(ScrollViewEventType.TYPE_LAYOUT_CHANGED, null);
        }
        super.onLayout(changed, l, t, r, b);
    }
    
    public void setListener(IEventScrollViewListener listener) {
        this.listener = listener;
    }
    
    public void init(int screenHeight, int excludeHeightOfBottom) {
        this.screenHeight = screenHeight;
        titleHeight = this.getResources().getDimensionPixelSize(R.dimen.title_bar_height);
        if (excludeHeightOfBottom > this.excludeHeightOfBottom) {
            this.excludeHeightOfBottom = excludeHeightOfBottom;
        }
    }
    
 // 滑动距离及坐标
    private float xDistance, yDistance, xLast, yLast;
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
}
