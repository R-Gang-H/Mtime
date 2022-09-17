package com.mtime.base.utils;

import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

/**
 * Created by liuyu on 2017/4/26.
 */

public class ViewUtils {
    /**
     * 用于设置TextView的文本内容
     *
     * @param textView
     * @param text
     */
    public static void setText(TextView textView, String text) {
        textView.setText(TextUtils.isEmpty(text) ? "" : text);
    }

    public static void showView(View view) {
        if (view != null) {
            view.setVisibility(View.VISIBLE);
        }
    }

    public static void hiddenView(View view) {
        if (view != null) {
            view.setVisibility(View.GONE);
        }
    }

    public static void setViewInvisable(View view) {
        if (view != null) {
            view.setVisibility(View.GONE);
        }
    }
    
    /**
     * 适配沉浸式状态栏，为view预留出状态拦的高度
     * @param view
     * @param changeHeight 改变view的高度来达到适配
     */
    public static void applyInsets(View view, final boolean changeHeight) {
        if(null != view) {
            ViewCompat.requestApplyInsets(view);
            ViewCompat.setOnApplyWindowInsetsListener(view, new OnApplyWindowInsetsListener() {
                @Override
                public WindowInsetsCompat onApplyWindowInsets(View v, WindowInsetsCompat insets) {
                    Object applied = v.getTag(R.id.view_tag_apply_insets);
                    if(null == applied) {
                        v.setTag(R.id.view_tag_apply_insets, true);
                        if (changeHeight) {
                            v.getLayoutParams().height += insets.getSystemWindowInsetTop();
                        }
                        v.setPadding(v.getPaddingLeft(), v.getPaddingTop() + insets.getSystemWindowInsetTop(),
                                v.getPaddingRight(), v.getPaddingBottom() + insets.getSystemWindowInsetBottom());
                    }
                    return insets;
                }
            });
        }
    }
}
