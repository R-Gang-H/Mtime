package com.mtime.bussiness.mine.login.holder;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kk.taurus.uiframe.i.ITitleBar;
import com.kk.taurus.uiframe.v.ContentHolder;
import com.mtime.R;
import com.mtime.bussiness.mine.login.widget.LoginSmsCodeView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @author vivian.wei
 * @date 2019/8/16
 * @desc 手机绑定页Holder
 */
public class BindPhoneWithLoginHolder extends ContentHolder<Void> {

    @BindView(R.id.activity_bind_phone_back_iv)
    ImageView mBackIv;
    @BindView(R.id.activity_bind_phone_title_tv)
    TextView mTitleTv;
    @BindView(R.id.activity_bind_phone_tip_tv)
    TextView mTipTv;
    @BindView(R.id.login_sms_code_view_root_cl)
    View mSmsCodeRootCl;

    private Unbinder mUnbinder;
    private LoginSmsCodeView mLoginSmsCodeView;
    private final LoginSmsCodeView.ILoginSmsCodeViewClickListener mILoginSmsCodeViewClickListener;

    public BindPhoneWithLoginHolder(Context context, LoginSmsCodeView.ILoginSmsCodeViewClickListener listener) {
        super(context);
        mILoginSmsCodeViewClickListener = listener;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onHolderCreated() {
        super.onHolderCreated();
        setContentView(R.layout.activity_bind_phone);
        initView();
        initListener();
    }

    private void initView() {
        mUnbinder = ButterKnife.bind(this, mRootView);

        mLoginSmsCodeView = new LoginSmsCodeView(mContext, mSmsCodeRootCl, mILoginSmsCodeViewClickListener);
        mLoginSmsCodeView.showTip(false);
        mLoginSmsCodeView.setBtnText(getResource().getString(R.string.ok));
    }

    private void initListener() {
        mBackIv.setOnClickListener(this);
    }

    @Override
    public void refreshView() {
        super.refreshView();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_bind_phone_back_iv:
                // 返回箭头
                onHolderEvent(ITitleBar.TITLE_BAR_EVENT_NAVIGATION_CLICK, null);
                break;
            default:
                break;
        }
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

    // 设置标题文字
    public void setTitle(String title) {
        if(mTitleTv != null) {
            mTitleTv.setText(title);
        }
    }

    // 隐藏标题下方的提示
    public void hideTopTip() {
        if(mTipTv != null) {
            mTipTv.setVisibility(View.GONE);
        }
    }

    // 设置默认手机号（身份验证页使用）
    public void setDefaultMobile(String mobile) {
        if(mLoginSmsCodeView != null) {
            mLoginSmsCodeView.setDefaultMobile(mobile);
        }
    }

    // 设置手机文本框不能编辑（身份验证页使用）
    public void enablePhoneEt(boolean enable) {
        if(mLoginSmsCodeView != null) {
            mLoginSmsCodeView.enablePhoneEt(enable);
        }
    }

    // 发送短信验证码成功
    public void sendSmsCodeSuccess(String smsCodeId) {
        if(mLoginSmsCodeView != null) {
            mLoginSmsCodeView.sendSmsCodeSuccess(smsCodeId);
        }
    }

    // 直接显示返回的图片验证码
    public void showImgVCodeDlg(String imgCodeId, String imgCode) {
        if(mLoginSmsCodeView != null) {
            mLoginSmsCodeView.showImgVCodeDlg(imgCodeId, imgCode);
        }
    }

}
