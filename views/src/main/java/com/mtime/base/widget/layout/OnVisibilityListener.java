package com.mtime.base.widget.layout;

/**
 * Created by Taurus on 2017/11/30.
 */

public interface OnVisibilityListener {
    void onVisibilityChange(int visibility);
    void onAttachedToWindow();
    void onDetachedFromWindow();
}
