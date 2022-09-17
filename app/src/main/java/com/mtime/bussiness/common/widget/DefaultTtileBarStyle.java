package com.mtime.bussiness.common.widget;

import android.graphics.Color;
import android.os.Build;
import android.util.TypedValue;

import com.mtime.R;
import com.mtime.base.views.titlebar.ITitleBarStyle;
import com.mtime.frame.App;

/**
 * @author ZhouSuQiang
 * @date 2018/10/16
 */
public class DefaultTtileBarStyle implements ITitleBarStyle {
    @Override
    public int getTitleBarHeight() {
        return App.get().getResources().getDimensionPixelOffset(R.dimen.title_bar_height);
    }
    
    @Override
    public int getBackgroundColor() {
        return Color.TRANSPARENT;
    }
    
    @Override
    public int getBackIconResource() {
        return R.drawable.common_icon_title_back;
    }
    
    @Override
    public int getLeftViewColor() {
        return 0x1D2736;
    }
    
    @Override
    public int getTitleViewColor() {
        return 0x1D2736;
    }
    
    @Override
    public int getRightViewColor() {
        return 0x1D2736;
    }
    
    @Override
    public float getLeftViewSize() {
        return 15;
    }
    
    @Override
    public float getTitleViewSize() {
        return 17;
    }
    
    @Override
    public float getRightViewSize() {
        return 15;
    }
    
    @Override
    public boolean getLineVisibility() {
        return false;
    }
    
    @Override
    public int getLineBackgroundColor() {
        return 0xFF333333;
    }
    
    private static int selectableItemBackgroundResId;
    @Override
    public int getLeftViewBackground() {
        return getSystemSelectableItemBackgroundResId();
    }
    
    @Override
    public int getRightViewBackground() {
        return getSystemSelectableItemBackgroundResId();
    }
    
    /**
     * 获取系统水波纹效果资源ID
     * 点击有水波纹效果(android:background="?attr/selectableItemBackground")
     * @return
     */
    public static int getSystemSelectableItemBackgroundResId() {
        if(selectableItemBackgroundResId == 0) {
            int attrId = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ?
                    android.R.attr.selectableItemBackgroundBorderless : android.R.attr.selectableItemBackground;
            TypedValue typedValue = new TypedValue();
            App.get().getTheme().resolveAttribute(attrId, typedValue, true);
            selectableItemBackgroundResId = typedValue.resourceId;
        }
        return selectableItemBackgroundResId;
    }
}
