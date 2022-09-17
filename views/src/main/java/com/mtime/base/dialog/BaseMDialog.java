package com.mtime.base.dialog;

import android.os.Bundle;
import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.mtime.base.recyclerview.CommonViewHolder;
import com.mtime.base.utils.MScreenUtils;
import com.mtime.base.views.R;

/**
 * Dialog基类
 * 参考 {https://github.com/Othershe/NiceDialog} V1.1.4
 */
public abstract class BaseMDialog extends DialogFragment {
    private static final String MARGIN = "mMargin";
    private static final String WIDTH = "mWidth";
    private static final String HEIGHT = "mHeight";
    private static final String DIM = "dim_amount";
    private static final String BOTTOM = "show_bottom";
    private static final String CANCEL = "out_cancel";
    private static final String ANIM = "anim_style";
    private static final String LAYOUT = "layout_id";

    private int mMargin;//左右边距
    private int mWidth;//宽度
    private int mHeight;//高度
    private float mDimAmount = 0.5f;//灰度深浅
    private boolean mShowBottom;//是否底部显示
    private boolean mOutCancel = true;//是否点击外部取消
    @StyleRes
    private int mAnimStyle;
    @LayoutRes
    protected int mLayoutId;

    public abstract int intLayoutId();

    public abstract void convertView(CommonViewHolder holder, BaseMDialog dialog);

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.BaseMDialog);
        mLayoutId = intLayoutId();

        //恢复保存的数据
        if (savedInstanceState != null) {
            mMargin = savedInstanceState.getInt(MARGIN);
            mWidth = savedInstanceState.getInt(WIDTH);
            mHeight = savedInstanceState.getInt(HEIGHT);
            mDimAmount = savedInstanceState.getFloat(DIM);
            mShowBottom = savedInstanceState.getBoolean(BOTTOM);
            mOutCancel = savedInstanceState.getBoolean(CANCEL);
            mAnimStyle = savedInstanceState.getInt(ANIM);
            mLayoutId = savedInstanceState.getInt(LAYOUT);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(mLayoutId, container, false);
        convertView(CommonViewHolder.get(view), this);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        initParams();
    }

    /**
     * 屏幕旋转等导致DialogFragment销毁后重建时保存数据
     *
     * @param outState
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(MARGIN, mMargin);
        outState.putInt(WIDTH, mWidth);
        outState.putInt(HEIGHT, mHeight);
        outState.putFloat(DIM, mDimAmount);
        outState.putBoolean(BOTTOM, mShowBottom);
        outState.putBoolean(CANCEL, mOutCancel);
        outState.putInt(ANIM, mAnimStyle);
        outState.putInt(LAYOUT, mLayoutId);
    }

    private void initParams() {
        Window window = getDialog().getWindow();
        if (window != null) {
            WindowManager.LayoutParams lp = window.getAttributes();
            //调节灰色背景透明度[0-1]，默认0.5f
            lp.dimAmount = mDimAmount;
            //是否在底部显示
            if (mShowBottom) {
                lp.gravity = Gravity.BOTTOM;
                if (mAnimStyle == 0) {
                    mAnimStyle = R.style.MDialogDefaultBottomAnimation;
                }
            }

            //设置dialog宽度
            if (mWidth == 0) {
                lp.width = MScreenUtils.getScreenWidth(getContext()) - 2 * MScreenUtils.dp2px(getContext(), mMargin);
            } else if (mWidth == -1) {
                lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
            } else {
                lp.width = MScreenUtils.dp2px(getContext(), mWidth);
            }

            //设置dialog高度
            if (mHeight == 0) {
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            } else {
                lp.height = MScreenUtils.dp2px(getContext(), mHeight);
            }
            
            //设置dialog进入、退出的动画
            window.setWindowAnimations(mAnimStyle);
            window.setAttributes(lp);
        }
        setCancelable(mOutCancel);
    }

    public BaseMDialog setMargin(int margin) {
        this.mMargin = margin;
        return this;
    }

    public BaseMDialog setWidth(int width) {
        this.mWidth = width;
        return this;
    }

    public BaseMDialog setHeight(int height) {
        this.mHeight = height;
        return this;
    }

    public BaseMDialog setDimAmount(float dimAmount) {
        this.mDimAmount = dimAmount;
        return this;
    }

    public BaseMDialog setShowBottom(boolean showBottom) {
        this.mShowBottom = showBottom;
        return this;
    }
    
    public BaseMDialog setOutCancel(boolean outCancel) {
        this.mOutCancel = outCancel;
        return this;
    }

    public BaseMDialog setAnimStyle(@StyleRes int animStyle) {
        this.mAnimStyle = animStyle;
        return this;
    }

    public BaseMDialog show(FragmentManager manager) {
        FragmentTransaction ft = manager.beginTransaction();
        if (this.isAdded()) {
            ft.remove(this).commit();
        }
        ft.add(this, String.valueOf(System.currentTimeMillis()));
        ft.commitAllowingStateLoss();
        return this;
    }
}
