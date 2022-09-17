package com.mtime.widgets;

import android.content.Context;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZhouSuQiang on 2017/9/19.
 */

public class MLinearLayout extends LinearLayout {
    
    private final List<OnVisibilityChangedLisnner> mOnVisibilityChangedLisnners = new ArrayList<>();
    
    public MLinearLayout(Context context) {
        super(context);
    }
    
    public MLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }
    
    public MLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public MLinearLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
    
    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        for (OnVisibilityChangedLisnner lisnner : mOnVisibilityChangedLisnners) {
            lisnner.onVisibilityChanged(this, visibility);
        }
    }
    
    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
    }
    
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }
    
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
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
