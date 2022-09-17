package com.mtime.bussiness.common.widget;

import android.content.Context;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.mtime.R;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshKernel;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.internal.ProgressDrawable;

import java.util.List;

/**
 * Created by zhuqiguang on 2017/12/29.
 * 下拉刷新头
 */
public class MtimeRefreshHeader extends FrameLayout implements RefreshHeader {
    private boolean isSupportRefreshTip;
    private boolean isFinish = true;

    private TextView mHeaderText;//标题文本
    private ImageView mArrowView;//下拉箭头
    private ImageView mProgressView;//刷新动画视图
    private ProgressDrawable mProgressDrawable;//刷新动画
    private View headerView;

    private View mRefreshTipView; //刷新完成提示
    private TextView mRefreshTipTextView; //刷新完成后提示的文本view
    private CharSequence mTipText; //提示的文本

    public MtimeRefreshHeader(Context context) {
        super(context);
        initView(context);
    }

    public MtimeRefreshHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initView(context);
    }

    public MtimeRefreshHeader(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initView(context);
    }

    private void initView(Context context) {
        headerView = LayoutInflater.from(context).inflate(R.layout.layout_refresh_header, this, false);
        mHeaderText = headerView.findViewById(R.id.layout_fresh_tv);
        mArrowView = headerView.findViewById(R.id.layout_arrow_iv);
        mProgressView = headerView.findViewById(R.id.layout_progress_iv);
        mProgressDrawable = new ProgressDrawable();
        mProgressView.setImageDrawable(mProgressDrawable);
        addView(headerView);

        mRefreshTipView = LayoutInflater.from(context).inflate(R.layout.common_layout_refresh_tip, this, false);
        mRefreshTipTextView = mRefreshTipView.findViewById(R.id.common_layout_refresh_tip_tv);
        mRefreshTipView.setVisibility(INVISIBLE);
        addView(mRefreshTipView);
    }

    private int size(List newData) {
        if (newData != null)
            return newData.size();
        return 0;
    }

    private String nullTip() {
        return getString(R.string.home_feed_top_hint_zero_text);
    }

    private String sizeTip(int size) {
        return String.format(getString(R.string.home_feed_top_hint_count_text), size);
    }

    private String getString(int id) {
        return getContext().getString(id);
    }

    public void showTip(List newData) {
        int size = size(newData);
        showTip(size);
    }

    public void showTip(int size) {
        String tip;
        if (size <= 0) {
            tip = nullTip();
        } else {
            tip = sizeTip(size);
        }
        showTip(tip);
    }

    public void showTipNoDataTip() {
        showTip(nullTip());
    }

    public void showTipMaybeNetErrorTip() {
        showTip(getString(R.string.home_feed_top_hint_failure_text));
    }

    public void showTip(CharSequence tip) {
        if (isFinish) {
            mRefreshTipTextView.setText(tip);
        } else {
            mTipText = tip;
        }
    }

    /**
     * 设置支持刷新完成后提示
     * @param supportRefreshTip
     */
    public void setSupportRefreshTip(boolean supportRefreshTip) {
        isSupportRefreshTip = supportRefreshTip;
    }

    @Override
    @NonNull
    public View getView() {
        return this;//真实的视图就是自己，不能返回null
    }

    @Override
    public SpinnerStyle getSpinnerStyle() {
        return SpinnerStyle.Translate;//指定为平移，不能null
    }

    @Override
    public void onStartAnimator(RefreshLayout layout, int headHeight, int extendHeight) {
        mProgressDrawable.start();//开始动画
    }

    @Override
    public int onFinish(RefreshLayout layout, boolean success) {
        isFinish = true;

        mProgressDrawable.stop();//停止动画

        if (isSupportRefreshTip) {
            if (!success) {
                mTipText = getString(R.string.home_feed_top_hint_failure_text);
            }

            if (!TextUtils.isEmpty(mTipText)) {
                headerView.setVisibility(INVISIBLE);
                mRefreshTipView.setVisibility(VISIBLE);
                mRefreshTipTextView.setText(mTipText);
                mRefreshTipTextView.setScaleX(0.95f);
                mRefreshTipTextView.setScaleY(0.95f);
                mRefreshTipTextView.animate().scaleX(1f).scaleY(1f)
                        .setDuration(300)
                        .setInterpolator(new OvershootInterpolator())
                        .start();
                mTipText = null;
                return 2000;//延迟之后再弹回
            }
        }

        if (success) {
            mHeaderText.setText("刷新完成");
        } else {
            mHeaderText.setText("刷新失败");
        }
        return 100;//延迟之后再弹回
    }

    @Override
    public void onStateChanged(RefreshLayout refreshLayout, RefreshState oldState, RefreshState newState) {
        switch (newState) {
            case None:
            case PullDownToRefresh:
                isFinish = false;
                headerView.setVisibility(VISIBLE);
                mRefreshTipView.setVisibility(INVISIBLE);
                mHeaderText.setText("下拉刷新");
                mArrowView.setVisibility(VISIBLE);//显示下拉箭头
                mProgressView.setVisibility(GONE);//隐藏动画
                mArrowView.animate().rotation(0);//还原箭头方向
                break;
            case Refreshing:
                mHeaderText.setText("正在刷新");
                mProgressView.setVisibility(VISIBLE);//显示加载动画
                mArrowView.setVisibility(GONE);//隐藏箭头
                break;
            case ReleaseToRefresh:
                mHeaderText.setText("释放更新");
                mArrowView.animate().rotation(180);//显示箭头改为朝上
                break;
        }
    }

    @Override
    public boolean isSupportHorizontalDrag() {
        return false;
    }

    @Override
    public void onInitialized(RefreshKernel kernel, int height, int extendHeight) {
    }

    @Override
    public void onMoving(boolean isDragging, float percent, int offset, int height, int maxDragHeight) {

    }

    @Override
    public void onReleased(@NonNull RefreshLayout refreshLayout, int height, int maxDragHeight) {

    }

    @Override
    public void onHorizontalDrag(float percentX, int offsetX, int offsetMax) {
    }

    @Override
    public void setPrimaryColors(@ColorInt int... colors) {
    }
}