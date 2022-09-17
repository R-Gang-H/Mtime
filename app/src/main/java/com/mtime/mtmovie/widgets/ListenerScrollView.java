package com.mtime.mtmovie.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 * Created by guangshun on 15-4-29.
 */
public class ListenerScrollView extends ScrollView {

    private ScrollListener scrollListener;

    public ListenerScrollView (Context context) {
        super(context);
    }

    public ListenerScrollView (Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ListenerScrollView (Context context, AttributeSet attrs,int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onScrollChanged(int l, int t, int ol, int ot){
        if(this.scrollListener!=null){
            this.scrollListener.scrollChanged(t);
        }
    }

    public void setScrollListener(ScrollListener scrollListener){
        this.scrollListener = scrollListener;
    }

    public interface ScrollListener{
        void scrollChanged(int t);
    }
}
