package com.mtime.bussiness.common.widget;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;
import android.view.View;

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2019/3/15
 * https://blog.csdn.net/cuenca/article/details/52356956
 */
public class ScalePageTransformer implements ViewPager.PageTransformer {

    public static float MIN_SCALE = 0.9f;


    public ScalePageTransformer() {
        this(0.9f);
    }

    public ScalePageTransformer(float minScale) {
        MIN_SCALE = minScale;
    }

    /**
     * 重新计算 page 的位置transformPos，使得
     * 当page居中时 transformPos为0
     * 当page向左偏移时 transformPos为[-Infinity, 0)
     * 当page向右偏移时 transformPos为(0, +Infinity]
     */
    @Override
    public void transformPage(@NonNull View page, float position) {
        ViewPager viewPager = (ViewPager) page.getParent();
        int scrollX = viewPager.getScrollX();
        int clientWidth = viewPager.getMeasuredWidth() -
                viewPager.getPaddingLeft() - viewPager.getPaddingRight();
        int offsetX = page.getLeft() - scrollX;
        int parentWidth = viewPager.getMeasuredWidth();
        int childWidth = page.getWidth();
        float deltaX = (float) (parentWidth - childWidth) / 2;
        float transformPos = (offsetX - deltaX) / clientWidth;

        if (transformPos < -1) { // [-Infinity,-1)
            page.setScaleX(MIN_SCALE);
            page.setScaleY(MIN_SCALE);
            page.setPivotX(clientWidth);
            page.setPivotY(page.getHeight() / 2f);
        } else if (transformPos <= 1) { // [-1,1]
            float scaleFactor = MIN_SCALE
                    + (1f - MIN_SCALE) * (1f - Math.abs(transformPos));
            page.setScaleX(scaleFactor);
            page.setScaleY(scaleFactor);
            page.setPivotX(transformPos < 0 ? clientWidth : viewPager.getPaddingRight() / 4f);
            page.setPivotY(page.getHeight() / 2f);
        } else { // (1,+Infinity]
            page.setScaleX(MIN_SCALE);
            page.setScaleY(MIN_SCALE);
            page.setPivotX(viewPager.getPaddingRight() / 4f);
            page.setPivotY(page.getHeight() / 2f);
        }
    }
}
