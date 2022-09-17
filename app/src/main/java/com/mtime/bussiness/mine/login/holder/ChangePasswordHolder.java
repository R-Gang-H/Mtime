package com.mtime.bussiness.mine.login.holder;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kk.taurus.uiframe.i.ITitleBar;
import com.kk.taurus.uiframe.v.ContentHolder;
import com.mtime.R;
import com.mtime.bussiness.mine.login.widget.LoginSmsCodeView;
import com.mtime.bussiness.mine.profile.activity.ChangePasswordActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @author vivian.wei
 * @date 2019/8/22
 * @desc 修改密码页Holder
 */
public class ChangePasswordHolder extends ContentHolder<Void> {

    // 标题
    @BindView(R.id.activity_change_password_back_iv)
    ImageView mBackIv;
    // 验证码组件
    @BindView(R.id.login_sms_code_view_root_cl)
    View mSmsCodeRootCl;
    @BindView(R.id.activity_change_password_modify_account_password_tv)
    TextView mModifyAccountPasswordTv;

    private Unbinder mUnbinder;
    private LoginSmsCodeView mLoginSmsCodeView;
    private final LoginSmsCodeView.ILoginSmsCodeViewClickListener mILoginSmsCodeViewClickListener;

    public ChangePasswordHolder(Context context, LoginSmsCodeView.ILoginSmsCodeViewClickListener listener) {
        super(context);
        mILoginSmsCodeViewClickListener = listener;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onHolderCreated() {
        super.onHolderCreated();
        setContentView(R.layout.activity_change_password);
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
        mModifyAccountPasswordTv.setOnClickListener(this);
    }

    @Override
    public void refreshView() {
        super.refreshView();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_change_password_back_iv:
                // 返回箭头
                onHolderEvent(ITitleBar.TITLE_BAR_EVENT_NAVIGATION_CLICK, null);
                break;
            case R.id.activity_change_password_modify_account_password_tv:
                // 跳转登录密码修改页
                onHolderEvent(ChangePasswordActivity.HOLDER_EVENT_CODE_LOGIN, null);
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
