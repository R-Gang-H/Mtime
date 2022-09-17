package com.mtime.bussiness.common.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * 好像是什么悬浮效果，不太清楚，反正有地方在用
 *
 * @author heguangshun
 */
public class MoveLayout extends LinearLayout {

    private int init_height;

    public MoveLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        init_height = b - t;
    }

    public int getInit_height() {
        return init_height;
    }

    public void move(int value) {
        post(new Runnable() {
            @Override
            public void run() {
                ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) getLayoutParams();
                lp.topMargin = value;
                setLayoutParams(lp);
            }
        });
    }

}
