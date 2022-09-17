package com.mtime.bussiness.mine.login.holder;

import android.content.Context;
import android.text.InputType;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kk.taurus.uiframe.i.ITitleBar;
import com.kk.taurus.uiframe.v.ContentHolder;
import com.mtime.R;
import com.mtime.bussiness.mine.login.activity.RetrievePasswordActivity;
import com.mtime.bussiness.mine.login.widget.LoginSmsCodeView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @author vivian.wei
 * @date 2019/8/20
 * @desc 找回密码页Holder
 */
public class RetrievePasswordHolder extends ContentHolder<Void> {

    // 标题
    @BindView(R.id.activity_retrieve_password_back_iv)
    ImageView mBackIv;
    @BindView(R.id.activity_retrieve_password_title_logo_iv)
    ImageView mTitleLogoIv;
    @BindView(R.id.activity_retrieve_password_title_tv)
    TextView mTitleTv;
    // 顶部提示
    @BindView(R.id.activity_retrieve_password_tip_tv)
    TextView mTipTv;
    // 找回方式
    @BindView(R.id.activity_retrieve_password_way_list_ll)
    View mWayListLl;
    @BindView(R.id.activity_retrieve_password_by_phone_rl)
    View mByPhoneRl;
    @BindView(R.id.activity_retrieve_password_by_email_rl)
    View mByEmailRl;
    // 验证码组件
    @BindView(R.id.login_sms_code_view_root_cl)
    View mSmsCodeRootCl;

    private Unbinder mUnbinder;
    // 当前找回方式
    private int mWay = RetrievePasswordActivity.WAY_NONE;
    private LoginSmsCodeView mLoginSmsCodeView;
    private final LoginSmsCodeView.ILoginSmsCodeViewClickListener mILoginSmsCodeViewClickListener;

    public RetrievePasswordHolder(Context context, LoginSmsCodeView.ILoginSmsCodeViewClickListener listener) {
        super(context);
        mILoginSmsCodeViewClickListener = listener;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onHolderCreated() {
        super.onHolderCreated();
        setContentView(R.layout.activity_retrieve_password);
        initView();
        initListener();
    }

    private void initView() {
        mUnbinder = ButterKnife.bind(this, mRootView);

        mTitleTv.setVisibility(View.GONE);
        mTipTv.setVisibility(View.GONE);

        mLoginSmsCodeView = new LoginSmsCodeView(mContext, mSmsCodeRootCl, mILoginSmsCodeViewClickListener);
        mLoginSmsCodeView.showTip(false);
        mLoginSmsCodeView.setBtnText(getResource().getString(R.string.ok));
        mSmsCodeRootCl.setVisibility(View.GONE);
    }

    private void initListener() {
        mBackIv.setOnClickListener(this);
        mByPhoneRl.setOnClickListener(this);
        mByEmailRl.setOnClickListener(this);
    }

    @Override
    public void refreshView() {
        super.refreshView();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_retrieve_password_back_iv:
                // 返回箭头
                back();
                break;
            case R.id.activity_retrieve_password_by_phone_rl:
                // 手机号找回
                mWay = RetrievePasswordActivity.WAY_PHONE;
                showWayView();
                break;
            case R.id.activity_retrieve_password_by_email_rl:
                // 邮箱找回
                mWay = RetrievePasswordActivity.WAY_EMAIL;
                showWayView();
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

    // 显示找回布局
    private void showWayView() {
        if(mWay == RetrievePasswordActivity.WAY_NONE) {
            mTitleTv.setVisibility(View.GONE);
            mTipTv.setVisibility(View.GONE);
            mSmsCodeRootCl.setVisibility(View.GONE);
            mTitleLogoIv.setVisibility(View.VISIBLE);
            mWayListLl.setVisibility(View.VISIBLE);
            // 取消倒计时
            mLoginSmsCodeView.cancelViewTimer();
        } else {
            mTitleLogoIv.setVisibility(View.GONE);
            mWayListLl.setVisibility(View.GONE);
            mTitleTv.setVisibility(View.VISIBLE);
            if(mWay == RetrievePasswordActivity.WAY_PHONE) {
                mTitleTv.setText("身份验证");
                mTipTv.setVisibility(View.GONE);
                mLoginSmsCodeView.setAccountType(LoginSmsCodeView.ACCOUNT_TYPE_PHONE);
                mLoginSmsCodeView.setPhoneNumberEtHint("请输入手机号");
                mLoginSmsCodeView.setPhoneNumberEtInputType(InputType.TYPE_CLASS_NUMBER);
            } else if(mWay == RetrievePasswordActivity.WAY_EMAIL) {
                mLoginSmsCodeView.setAccountType(LoginSmsCodeView.ACCOUNT_TYPE_EMAIL);
                mTitleTv.setText("邮箱找回密码");
                mTipTv.setVisibility(View.VISIBLE);
                mLoginSmsCodeView.setPhoneNumberEtHint(getResource().getString(R.string.retrieve_email_hint));
                mLoginSmsCodeView.setPhoneNumberEtInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
            }
            mSmsCodeRootCl.setVisibility(View.VISIBLE);
        }
    }

    // 当前找回类型
    public int getType() {
        return mWay;
    }

    // 返回
    public void back() {
        if(mWay == RetrievePasswordActivity.WAY_NONE) {
            onHolderEvent(ITitleBar.TITLE_BAR_EVENT_NAVIGATION_CLICK, null);
        } else {
            mWay = RetrievePasswordActivity.WAY_NONE;
            showWayView();
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
