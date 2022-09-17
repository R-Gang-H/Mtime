package com.mtime.base.widget.banner;

import android.content.Context;
import android.os.Build;
import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * Created by ZhouSuQiang on 2017/12/7.
 * 默认的banner指示器
 */

public class DefaultBannerIndicator extends LinearLayout implements IBannerIndicator {

    private int mDefaultIndicatorRes;
    private int mFocusedIndicatorRes;
    private int mSpacing = 10;
    private int mPosition = 0;
    private int bottomMargin = 10;
    private int topMargin = 10;
    private int leftMargin = 10;
    private int rightMargin = 10;
    private boolean isSingleShow = false;

    public DefaultBannerIndicator(Context context) {
        this(context, null, 0);
    }

    public DefaultBannerIndicator(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DefaultBannerIndicator(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public DefaultBannerIndicator(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        setOrientation(HORIZONTAL);
    }

    public void setIndicatorRes(@DrawableRes int defaultIndicatorRes, @DrawableRes int focusedIndicatorRes) {
        mDefaultIndicatorRes = defaultIndicatorRes;
        mFocusedIndicatorRes = focusedIndicatorRes;
    }

    public void setSingleShow(boolean singleShow) {
        isSingleShow = singleShow;
    }

    public void setSpacing(int spacing) {
        mSpacing = spacing;
    }

    public void setMargin(int topMargin, int leftMargin, int rightMargin, int bottomMargin) {
        this.bottomMargin = bottomMargin;
        this.topMargin = topMargin;
        this.leftMargin = leftMargin;
        this.rightMargin = rightMargin;
        setMargin();
    }

    private void setMargin() {
        if (null != getLayoutParams()) {
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) getLayoutParams();
            params.topMargin = topMargin;
            params.bottomMargin = bottomMargin;
            params.leftMargin = leftMargin;
            params.rightMargin = rightMargin;
            setLayoutParams(params);
        }
    }

    @Override
    public View onCreateIndicatorView(LayoutInflater inflater, ViewGroup parent) {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        setLayoutParams(params);
        setMargin();
        return this;
    }

    @Override
    public void onNotifyDataChanged(int count) {
        removeAllViews();
        if (isSingleShow || count > 1) {
            ImageView item;
            for (int i = 0; i < count; i++) {
                item = new ImageView(getContext());
                item.setPadding(mSpacing / 2, 0, mSpacing / 2, 0);
                item.setImageResource(mDefaultIndicatorRes);
                addView(item);
            }
            onPageSelected(mPosition);
        }
    }

    @Override
    public void onPageSelected(int position) {
        setIndicatorRes(mPosition, true);
        mPosition = position;
        setIndicatorRes(mPosition, false);
    }

    private void setIndicatorRes(int position, boolean isDefaultRes) {
        View child = getChildAt(position);
        if (null != child && child instanceof ImageView) {
            ((ImageView) child).setImageResource(isDefaultRes ? mDefaultIndicatorRes : mFocusedIndicatorRes);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }
}
