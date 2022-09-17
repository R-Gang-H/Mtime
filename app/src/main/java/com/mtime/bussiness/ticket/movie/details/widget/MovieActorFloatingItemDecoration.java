package com.mtime.bussiness.ticket.movie.details.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import androidx.annotation.ColorInt;
import androidx.annotation.Dimension;
import androidx.annotation.DrawableRes;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mtime.base.utils.MScreenUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author vivian.wei
 * @date 2019/5/22
 * @desc 演职员表页Title悬浮的效果，只支持LinearLayoutManager垂直滑动的情况
 */
public class MovieActorFloatingItemDecoration extends RecyclerView.ItemDecoration {

    private Drawable mDivider;
    private int dividerHeight = 0;
    private int dividerWidth = 0;
    private final Map<Integer, CharSequence> keys = new HashMap<>();
    private final Map<CharSequence, View> titleViews = new HashMap<>();
    private int mTitleHeight;
    private int mTitleTextColor;
    private float mTitleTextSize;
    private boolean mBold;
    private boolean mCenter;
    private int mTitleBackgroundColor;
    private Context mContext;
    /**
     * 滚动列表的时候是否一直显示悬浮头部
     */
    private boolean showFloatingHeaderOnScrolling = true;
    private int mLeftOffset = 0;
    private int mTitleViewPaddingLeft = 0;

    public MovieActorFloatingItemDecoration(Context context) {
        init(context);
    }

    /**
     * 自定义分割线
     *
     * @param context
     * @param drawableId 分割线图片
     */
    public MovieActorFloatingItemDecoration(Context context, @DrawableRes int drawableId) {
        mDivider = ContextCompat.getDrawable(context, drawableId);
        this.dividerHeight = mDivider.getIntrinsicHeight();
        this.dividerWidth = mDivider.getIntrinsicWidth();
        init(context);
    }

    /**
     * 自定义分割线
     * 也可以使用{@link Canvas#drawRect(float, float, float, float, Paint)}或者{@link Canvas#drawText(String, float, float, Paint)}等等
     * 结合{@link Paint}去绘制各式各样的分割线
     *
     * @param context
     * @param color         整型颜色值，非资源id
     * @param dividerWidth  单位为dp
     * @param dividerHeight 单位为dp
     */
    public MovieActorFloatingItemDecoration(Context context, @ColorInt int color, @Dimension float dividerWidth, @Dimension float dividerHeight) {
        mDivider = new ColorDrawable(color);
        this.dividerWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dividerWidth, context.getResources().getDisplayMetrics());
        this.dividerHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dividerHeight, context.getResources().getDisplayMetrics());
        init(context);
    }

    private void init(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        drawVertical(c, parent);
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
        if (!showFloatingHeaderOnScrolling) {
            return;
        }
        int firstVisiblePos = ((LinearLayoutManager) parent.getLayoutManager()).findFirstVisibleItemPosition();
        if (firstVisiblePos == RecyclerView.NO_POSITION) {
            return;
        }
        CharSequence title = getTitle(firstVisiblePos);
        if (TextUtils.isEmpty(title)) {
            return;
        }
        boolean flag = false;
        if (getTitle(firstVisiblePos + 1) != null && !title.equals(getTitle(firstVisiblePos + 1))) {
            //说明是当前组最后一个元素，但不一定碰撞了
            View child = parent.findViewHolderForAdapterPosition(firstVisiblePos).itemView;
            if (child.getTop() + child.getMeasuredHeight() < mTitleHeight) {
                //进一步检测碰撞
                c.save();//保存画布当前的状态
                flag = true;
                c.translate(0, child.getTop() + child.getMeasuredHeight() - mTitleHeight);//负的代表向上
            }
        }
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();
        int top = parent.getPaddingTop();
        int bottom = top + mTitleHeight;

        drawTitleView(mContext, title, c, left, top, right, bottom);

        if (flag) {
            //还原画布为初始状态
            c.restore();
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int pos = parent.getChildViewHolder(view).getAdapterPosition();
        if (keys.containsKey(pos)) {//留出头部偏移
            outRect.set(0, mTitleHeight, 0, 0);
        } else {
            outRect.set(0, dividerHeight, 0, 0);
        }
    }

    /**
     * *如果该位置没有，则往前循环去查找标题，找到说明该位置属于该分组
     *
     * @param position
     * @return
     */
    private CharSequence getTitle(int position) {
        while (position >= 0) {
            if (keys.containsKey(position)) {
                return keys.get(position);
            }
            position--;
        }
        return null;
    }

    private void drawVertical(Canvas c, RecyclerView parent) {
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();
        int top = 0;
        int bottom = 0;
        for (int i = 0; i < parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            if (!keys.containsKey(params.getViewLayoutPosition())) {
                //画普通分割线
                if(null != mDivider) {
                    top = child.getTop() - params.topMargin - dividerHeight;
                    bottom = top + dividerHeight;
                    mDivider.setBounds(left, top, right, bottom);
                    mDivider.draw(c);
                }
            } else {
                //画头部
                top = child.getTop() - params.topMargin - mTitleHeight;
                bottom = top + mTitleHeight;
                CharSequence text = keys.get(params.getViewLayoutPosition());

                drawTitleView(mContext, text, c, left, top, right, bottom);
            }
        }
    }

    private void drawTitleView(Context context, CharSequence text, Canvas c, int left, int top, int right, int bottom) {
        TextView titleView;
        if(!titleViews.containsKey(text)) {
            titleView = new TextView(context);
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(MScreenUtils.getScreenWidth(), mTitleHeight);
            titleView.setLayoutParams(params);
            titleView.setGravity(mCenter ? Gravity.CENTER : Gravity.CENTER_VERTICAL);
            titleView.setBackgroundColor(mTitleBackgroundColor);
            titleView.setTextColor(mTitleTextColor);
            if(mBold) {
                titleView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            }
            titleView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTitleTextSize);
            titleView.setPadding(mTitleViewPaddingLeft, 0, 0, 0);
            titleView.setText(text);

            final int widthSpec = View.MeasureSpec.makeMeasureSpec(params.width,
                    params.width == ViewGroup.LayoutParams.WRAP_CONTENT ? View.MeasureSpec.AT_MOST : View.MeasureSpec.EXACTLY);
            final int heightSpec = View.MeasureSpec.makeMeasureSpec(params.height,
                    params.height == ViewGroup.LayoutParams.WRAP_CONTENT ? View.MeasureSpec.AT_MOST : View.MeasureSpec.EXACTLY);
            titleView.measure(widthSpec, heightSpec);

            titleViews.put(text, titleView);
        } else {
            titleView = (TextView) titleViews.get(text);
        }
        c.save();
        c.translate(left + mLeftOffset, top);
        titleView.layout(left, top, right, bottom);
        titleView.draw(c);
        c.restore();
    }

    public void setShowFloatingHeaderOnScrolling(boolean showFloatingHeaderOnScrolling) {
        this.showFloatingHeaderOnScrolling = showFloatingHeaderOnScrolling;
    }

    public void setTitles(Map<Integer, CharSequence> keys) {
        this.keys.clear();
        this.keys.putAll(keys);
    }

    public void appendTitles(Integer key, CharSequence text) {
        this.keys.put(key, text);
    }

    public void setTitleHeight(int titleHeight) {
        this.mTitleHeight = titleHeight;
    }

    public void setTitleTextSize(float textSize) {
        mTitleTextSize = textSize;
    }

    public void setTitleTextColor(int color) {
        mTitleTextColor = color;
    }

    public void setTitleTextBold(boolean bold) {
        mBold = bold;
    }

    // 文字是否居中
    public void setTitleTextGravity(boolean center) {
        mCenter = center;
    }

    public void setTtitleBackground(int color) {
        mTitleBackgroundColor = color;
    }

    public void setTitleViewmTitleViewPaddingLeft(int paddingLeft) {
        mTitleViewPaddingLeft = paddingLeft;
    }

    public void clearTitles() {
        this.keys.clear();
    }

    public void setLeftOffset(int leftOffset) {
        mLeftOffset = leftOffset;
    }

}
