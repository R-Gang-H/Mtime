package com.mtime.bussiness.ticket.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by guangshun on 15-6-4.
 */
public class CouponLayout extends ViewGroup {

    public CouponLayout(Context context) {
        super(context);
    }

    public CouponLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CouponLayout
            (Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int mViewGroupWidth = getMeasuredWidth()-20;//优惠控件预留20个像素出来
        boolean isShowMore = false;//是否需要显示最后的一条数据...
        int childCount = getChildCount() - 1;//因为数据加了...，所以优惠的元素只有Count－1
        View moreView = getChildAt(getChildCount() - 1);//最后一条数据是...
        int left = 0;
        int marginLeft = 12;//每条优惠数据的间隔
        View childView = null;
        for (int i = 0; i < childCount; i++) {
            childView = getChildAt(i);
            int width = childView.getMeasuredWidth();
            if (left + width+marginLeft > mViewGroupWidth) {
                isShowMore = true;
                break;
            }
            childView.layout(left, 0, left + width, childView.getMeasuredHeight());
            left += width+marginLeft;
        }
        if (isShowMore) {
            moreView.layout(left, 0, left + moreView.getMeasuredWidth(), moreView.getMeasuredHeight());
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        if(getChildAt(0)!=null){
            setMeasuredDimension(widthMeasureSpec,
                    resolveSize(getChildAt(0).getMeasuredHeight(), heightMeasureSpec));
        }else{
            setMeasuredDimension(widthMeasureSpec,
                    heightMeasureSpec);
        }

}
}
