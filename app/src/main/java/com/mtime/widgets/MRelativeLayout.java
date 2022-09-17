package com.mtime.widgets;

import android.content.Context;
import android.os.Build;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZhouSuQiang on 2017/9/19.
 */

public class MRelativeLayout extends RelativeLayout {
    
    private final List<OnVisibilityChangedLisnner> mOnVisibilityChangedLisnners = new ArrayList<>();
    
    public MRelativeLayout(Context context) {
        super(context);
    }
    
    public MRelativeLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }
    
    public MRelativeLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public MRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
    
    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        for(OnVisibilityChangedLisnner lisnner : mOnVisibilityChangedLisnners) {
            lisnner.onVisibilityChanged(this, visibility);
        }
    }
    
    public void addVisibilityChangedLisnner(OnVisibilityChangedLisnner lisnner) {
        mOnVisibilityChangedLisnners.add(lisnner);
    }
    
    public void removeVisibilityChangedLisnner(OnVisibilityChangedLisnner lisnner) {
        mOnVisibilityChangedLisnners.remove(lisnner);
    }
    
    public interface OnVisibilityChangedLisnner {
        void onVisibilityChanged(View view, int visibility);
    }
}
