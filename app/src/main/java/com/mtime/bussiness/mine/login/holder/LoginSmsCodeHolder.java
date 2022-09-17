package com.mtime.bussiness.mine.login.holder;

import android.content.Context;
import android.view.View;

import com.kk.taurus.uiframe.v.ContentHolder;
import com.mtime.R;
import com.mtime.bussiness.mine.login.widget.LoginSmsCodeView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @author vivian.wei
 * @date 2019/8/8
 * @desc 短信验证码登录Holder
 */
public class LoginSmsCodeHolder extends ContentHolder<Void> {

    @BindView(R.id.login_sms_code_view_root_cl)
    View mSmsCodeRootCl;

    private Unbinder mUnbinder;
    private LoginSmsCodeView mLoginSmsCodeView;
    private final LoginSmsCodeView.ILoginSmsCodeViewClickListener mILoginSmsCodeViewClickListener;

    public LoginSmsCodeHolder(Context context, LoginSmsCodeView.ILoginSmsCodeViewClickListener listener) {
        super(context);
        mILoginSmsCodeViewClickListener = listener;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onHolderCreated() {
        super.onHolderCreated();
        setContentView(R.layout.fragment_login_sms_code);
        initView();
    }

    private void initView() {
        mUnbinder = ButterKnife.bind(this, mRootView);

        mLoginSmsCodeView = new LoginSmsCodeView(mContext, mSmsCodeRootCl, mILoginSmsCodeViewClickListener);

    }

    @Override
    public void refreshView() {
        super.refreshView();
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

        if(mLoginSmsCodeView != null) {
            mLoginSmsCodeView = null;
        }

        if (null != mUnbinder) {
            mUnbinder.unbind();
        }
    }

    // 文本框内容发生改变
    public void setOnTextChangeListener(LoginSmsCodeView.OnEditTextChangeListener listener) {
        if(mLoginSmsCodeView != null) {
            mLoginSmsCodeView.setOnTextChangeListener(listener);
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
