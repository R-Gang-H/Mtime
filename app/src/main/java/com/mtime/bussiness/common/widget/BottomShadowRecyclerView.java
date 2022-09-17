package com.mtime.bussiness.common.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mtime.R;

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2019-11-19
 */
public class BottomShadowRecyclerView extends RecyclerView {
    private View mShadowView;
    private boolean mIsDrawShadow;
    private final Rect mTempRect = new Rect();

    public BottomShadowRecyclerView(@NonNull Context context) {
        this(context, null);
    }

    public BottomShadowRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BottomShadowRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(@NonNull Context context, @Nullable AttributeSet attrs) {
        String text = null;
        if (null != attrs) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.BottomShadowRecyclerView);
            text = a.getString(R.styleable.BottomShadowRecyclerView_bottom_shadow_hint_text);
            a.recycle();
        }

        setWillNotDraw(false);
        setClipToPadding(false);
        mShadowView = LayoutInflater.from(context).inflate(R.layout.common_layout_bottom_shadow_and_text, null);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
        );
        mShadowView.setLayoutParams(lp);
        TextView textView = mShadowView.findViewById(R.id.bottom_shadow_text_hint_tv);
        textView.setVisibility(TextUtils.isEmpty(text) ? GONE : VISIBLE);
    }

    @Override
    public boolean isInEditMode() {
        return true;
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        super.onMeasure(widthSpec, heightSpec);
        measureShadowView();
        layoutShadowView();
        if (getPaddingBottom() < mShadowView.getMeasuredHeight()) {
            setPadding(getPaddingLeft(), getPaddingTop(), getPaddingRight(), mShadowView.getMeasuredHeight());
        }
    }

    private void measureShadowView() {
        ViewGroup.LayoutParams lp = mShadowView.getLayoutParams();
        final int width = lp.width < 0 ? this.getMeasuredWidth() : lp.width;
        final int height = lp.height < 0 ? this.getMeasuredHeight() : lp.height;
        final int widthSpec = View.MeasureSpec.makeMeasureSpec(width,
                lp.width == ViewGroup.LayoutParams.WRAP_CONTENT ? View.MeasureSpec.AT_MOST : View.MeasureSpec.EXACTLY);
        final int heightSpec = View.MeasureSpec.makeMeasureSpec(height,
                lp.height == ViewGroup.LayoutParams.WRAP_CONTENT ? View.MeasureSpec.AT_MOST : View.MeasureSpec.EXACTLY);
        mShadowView.measure(widthSpec, heightSpec);
    }

    private void layoutShadowView() {
        mShadowView.layout(0, getMeasuredHeight()-mShadowView.getMeasuredHeight(), getMeasuredWidth(), getMeasuredHeight());
    }

    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);
        if (null != getAdapter() && getLayoutManager() instanceof LinearLayoutManager) {
            int position = ((LinearLayoutManager) getLayoutManager()).findLastVisibleItemPosition();
            if (position == getAdapter().getItemCount() - 1) {
                mIsDrawShadow = true;
                return;
            }
        }
        mIsDrawShadow = false;
    }

    @Override
    public void onDraw(Canvas c) {
        super.onDraw(c);
        if (mIsDrawShadow) {
            final View itemView = getChildAt(getChildCount()-1);
            if (null != itemView && null != getLayoutManager()) {
                getLayoutManager().getDecoratedBoundsWithMargins(itemView, mTempRect);
                int bottom = getMeasuredHeight() - mShadowView.getMeasuredHeight();
                c.save();
                c.translate(0, Math.max(mTempRect.bottom , bottom));
                mShadowView.draw(c);
                c.restore();
            }
        }
    }
}
