package com.mtime.bussiness.common.widget;

import android.content.Context;
import android.content.res.TypedArray;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.text.Html;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.mtime.R;

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2019-05-17
 */
public class CommonItemTitleView extends FrameLayout implements View.OnClickListener {

    private ConstraintLayout mRootLayout;
    private TextView mTitleView;
    private TextView mAllBtnView;
    private String titleText;
    private String allText;

    private OnClickListener mOnAllBtnClickListener;

    public CommonItemTitleView(Context context) {
        this(context, null);
    }

    public CommonItemTitleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CommonItemTitleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        if(null != attrs) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CommonItemTitleView);
            if( null != typedArray) {
                titleText = typedArray.getString(R.styleable.CommonItemTitleView_itv_title_text);
                allText = typedArray.getString(R.styleable.CommonItemTitleView_itv_all_text);
                typedArray.recycle();
            }
        }

        init(context);
    }

    @Override
    public boolean isInEditMode() {
        return true;
    }

    private void init(Context context) {
        inflate(context, R.layout.common_item_title_view_layout, this);

        mRootLayout = findViewById(R.id.common_item_title_view_root_layout);
        mTitleView = findViewById(R.id.common_item_title_view_text_tv);
        mAllBtnView = findViewById(R.id.common_item_title_view_all_tv);

        if(!TextUtils.isEmpty(titleText)) {
            mTitleView.setText(titleText);
        }
        if(!TextUtils.isEmpty(allText)) {
            mAllBtnView.setText(allText);
        } else {
            mAllBtnView.setVisibility(GONE);
        }

    }

    public ConstraintLayout getRootLayout() {
        return mRootLayout;
    }

    public void setAllBtnText(int resid) {
        mAllBtnView.setText(resid);
    }

    public void setAllBtnText(CharSequence text) {
        if (TextUtils.isEmpty(text)) {
            setAllBtnViewVisibility(false);
            return;
        }
        mAllBtnView.setText(text);
    }

    public void setTitleText(int resid) {
        mTitleView.setText(resid);
    }

    public void setTitleText(CharSequence text) {
        mTitleView.setText(text);
    }

    public void setTitleText(int resid, Object... formatArgs) {
        mTitleView.setText(getResources().getString(resid, formatArgs));
    }

    public void setTitleTextForHtml(int resid, Object... formatArgs) {
        mTitleView.setText(Html.fromHtml(getResources().getString(resid, formatArgs)));
    }

    public void setAllBtnViewVisibility(boolean visibility) {
        mAllBtnView.setVisibility(visibility ? VISIBLE : GONE);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.common_item_title_view_all_tv) {
            if (null != mOnAllBtnClickListener) {
                mOnAllBtnClickListener.onClick(v);
            }
        }
    }

    public void setOnAllBtnClickListener(OnClickListener onAllBtnClickListener) {
        mAllBtnView.setOnClickListener(this);
        mOnAllBtnClickListener = onAllBtnClickListener;
    }

    public void performAllBtnClick() {
        mAllBtnView.performClick();
    }
}
