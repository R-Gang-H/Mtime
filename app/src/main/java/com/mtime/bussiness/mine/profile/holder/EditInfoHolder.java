package com.mtime.bussiness.mine.profile.holder;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.kk.taurus.uiframe.v.ContentHolder;
import com.mtime.R;
import com.mtime.base.utils.MToastUtils;
import com.mtime.bussiness.mine.profile.activity.EditInfoActivity;

import androidx.core.content.ContextCompat;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * @author vivian.wei
 * @date 2020/9/21
 * @desc 编辑通用页面Holder
 */
public class EditInfoHolder extends ContentHolder<String> {
    public static final int EVENT_CODE_BACK = 1;
    public static final int EVENT_CODE_SAVE = 2;

    // 签名最多字数
    private static final int MAX_SIZE_SIGN = 150;

    @BindView(R.id.act_edit_info_title_tv)
    TextView mTitleTv;
    @BindView(R.id.act_edit_info_save_tv)
    TextView mSaveTv;
    @BindView(R.id.act_edit_info_et)
    EditText mEt;
    @BindView(R.id.act_edit_info_bottom_tip_tv)
    TextView mBottomTipTv;

    private Unbinder mUnbinder;
    private int mType;
    private int mMaxSize = 0;

    public EditInfoHolder(Context context) {
        super(context);
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onHolderCreated() {
        super.onHolderCreated();

        setContentView(R.layout.act_edit_info);
        initView();
        initEvent();
    }

    private void initView() {
        mUnbinder = ButterKnife.bind(this, mRootView);
    }

    // 初始化事件
    private void initEvent() {
        mEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String des = s.toString().trim();
                if(des.length() > mMaxSize) {
                    mSaveTv.setTextColor(ContextCompat.getColor(mContext, R.color.color_AAB7C7_20_alpha));
                    mSaveTv.setClickable(false);
                    MToastUtils.showShortToast(String.format("最多%d个字", mMaxSize));
                } else {
                    mSaveTv.setTextColor(ContextCompat.getColor(mContext, R.color.color_20A0DA));
                    mSaveTv.setClickable(true);
                }
            }
        });
    }

    @Override
    public void refreshView() {
        super.refreshView();

        mEt.setText(mData);
        mEt.setFocusable(true);
        mEt.setFocusableInTouchMode(true);
        mEt.requestFocus();
        mEt.setSelection(mEt.getText().length()); // 将光标移至最后
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (null != mUnbinder) {
            mUnbinder.unbind();
        }
    }

    @OnClick({
            R.id.act_edit_info_back_iv,
            R.id.act_edit_info_save_tv,
    })
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.act_edit_info_back_iv: // 返回
                onHolderEvent(EVENT_CODE_BACK, null);
                break;
            case R.id.act_edit_info_save_tv: // 保存
                Bundle bundle = new Bundle();
                bundle.putString(EditInfoActivity.KEY_INFO_EDIT_CONTENT, mEt.getText().toString().trim());
                onHolderEvent(EVENT_CODE_SAVE, bundle);
                break;
            default:
                break;
        }
    }

    /**
     * 设置编辑内容类型
     * @param type
     */
    public void setType(int type) {
        mType = type;
        switch (mType) {
            case EditInfoActivity.EDIT_INFO_TYPE_SIGN:
                mTitleTv.setText(getResource().getString(R.string.edit_sign_title));
                mEt.setHint(getResource().getString(R.string.edit_sign_hint));
                mBottomTipTv.setText(getResource().getString(R.string.edit_sign_bottom_tip));
                mMaxSize = MAX_SIZE_SIGN;
                break;
            default:
                break;
        }
    }

}
