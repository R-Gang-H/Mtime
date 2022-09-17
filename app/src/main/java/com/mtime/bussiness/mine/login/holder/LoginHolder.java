package com.mtime.bussiness.mine.login.holder;

import android.content.Context;
import android.graphics.Paint;

import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jeremyliao.liveeventbus.LiveEventBus;
import com.kk.taurus.uiframe.i.ITitleBar;
import com.kk.taurus.uiframe.v.ContentHolder;
import com.kotlin.android.app.router.liveevent.event.LoginAgreeState;
import com.mtime.R;
import com.mtime.base.utils.MToastUtils;
import com.mtime.bussiness.mine.login.activity.LoginActivity;
import com.mtime.bussiness.mine.login.fragment.LoginAccountFragment;
import com.mtime.bussiness.mine.login.fragment.LoginSmsCodeFragment;
import com.mtime.bussiness.splash.LoadManager;
import com.mtime.statistic.large.h5.StatisticH5;
import com.mtime.util.JumpUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.kotlin.android.router.liveevent.EventKeyExtKt.LOGIN_AGREE_STATE;
import static com.mtime.constant.Constants.BUNDLE_KEY_AGREE_CHECK;

/**
 * @author vivian.wei
 * @date 2019/7/31
 * @desc 登录页Holder：短信验证码登录|账号密码登录
 */
public class LoginHolder extends ContentHolder<Void> {

    @BindView(R.id.activity_login_back_iv)
    ImageView mBackIv;
    @BindView(R.id.activity_login_account_login_link_tv)
    TextView mAccountLoginLinkTv;
    @BindView(R.id.activity_login_forget_password_tv)
    TextView mForgetPasswordTv;
    @BindView(R.id.activity_login_sms_code_login_link_tv)
    TextView mSmsCodeLoginLinkTv;
    @BindView(R.id.activity_login_bottom_service_tv)
    TextView mServiceTv;
    @BindView(R.id.activity_login_bottom_privacy_tv)
    TextView mPrivacyTv;
    @BindView(R.id.activity_login_bottom_agree_checkbox)
    AppCompatCheckBox mAgreeChecbox;
    @BindView(R.id.activity_login_wechat_iv)
    ImageView mWeChatIv;
    @BindView(R.id.activity_login_weibo_iv)
    ImageView mWeiboIv;
    @BindView(R.id.activity_login_qq_iv)
    ImageView mQqIv;

    private Unbinder mUnbinder;
    private final FragmentManager mFragmentManager;
    private LoginSmsCodeFragment mSmsCodefragment;
    private LoginAccountFragment mAccountFragment;

    public LoginHolder(Context context, FragmentManager fragmentManager) {
        super(context);
        mFragmentManager = fragmentManager;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onHolderCreated() {
        super.onHolderCreated();
        setContentView(R.layout.activity_login);
        initView();
        initListener();
    }

    private void initView() {
        mUnbinder = ButterKnife.bind(this, mRootView);

        //下划线
        mAccountLoginLinkTv.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        mSmsCodeLoginLinkTv.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        mServiceTv.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        mPrivacyTv.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);

        // 设置默认的Fragment
        showSmsCodeFragment();

    }

    private void initListener() {
        mBackIv.setOnClickListener(this);
        mAccountLoginLinkTv.setOnClickListener(this);
        mSmsCodeLoginLinkTv.setOnClickListener(this);
        mForgetPasswordTv.setOnClickListener(this);
        mServiceTv.setOnClickListener(this);
        mPrivacyTv.setOnClickListener(this);
        mWeChatIv.setOnClickListener(this);
        mWeiboIv.setOnClickListener(this);
        mQqIv.setOnClickListener(this);
        mAgreeChecbox.setOnCheckedChangeListener((buttonView, isChecked) ->
                LiveEventBus.get(LOGIN_AGREE_STATE).post(new LoginAgreeState(isChecked))
        );
    }

    @Override
    public void refreshView() {
        super.refreshView();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_login_back_iv:
                // 返回箭头
                onHolderEvent(ITitleBar.TITLE_BAR_EVENT_NAVIGATION_CLICK, null);
                break;
            case R.id.activity_login_account_login_link_tv:
                // 账号密码登录
                showAccountFragment();
                onHolderEvent(LoginActivity.HOLDER_EVENT_CODE_ACCOUNT_LOGIN_LINK_CLICK, null);
                break;
            case R.id.activity_login_sms_code_login_link_tv:
                // 短信验证码登录
                showSmsCodeFragment();
                onHolderEvent(LoginActivity.HOLDER_EVENT_CODE_SMS_CODE_LOGIN_LINK_CLICK, null);
                break;
            case R.id.activity_login_forget_password_tv:
                // 跳转找回密码页
                onHolderEvent(LoginActivity.HOLDER_EVENT_FORGET_PASSWORD_LINK_CLICK, null);
                JumpUtil.startRetrievePasswordActivity(mContext, "");
                finish();
                break;
            case R.id.activity_login_bottom_service_tv:
                // 时光网用户协议
                JumpUtil.startCommonWebActivity(mContext, LoadManager.getRegisterServiceUrl(), StatisticH5.PN_H5, null,
                        true, true, true, false, null);
                break;
            case R.id.activity_login_bottom_privacy_tv:
                // 隐私政策
                JumpUtil.startCommonWebActivity(mContext, LoadManager.getRegisterPrivacyUrl(), StatisticH5.PN_H5, null,
                        true, true, true, false, null);
                break;
            case R.id.activity_login_wechat_iv: // 微信
                if(checkAgree()) {
                    onHolderEvent(LoginActivity.HOLDER_EVENT_CODE_WECHAT, null);
                }
                break;
            case R.id.activity_login_weibo_iv:  // 微博
                if(checkAgree()) {
                    onHolderEvent(LoginActivity.HOLDER_EVENT_CODE_WEIBO, null);
                }
                break;
            case R.id.activity_login_qq_iv:     // QQ
                if(checkAgree()) {
                    onHolderEvent(LoginActivity.HOLDER_EVENT_CODE_QQ, null);
                }
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

    /**
     * 检查同意条款是否选中
     * @return
     */
    private boolean checkAgree() {
        if(!mAgreeChecbox.isChecked()) {
            MToastUtils.showShortToast(getString(R.string.checkbox_hint_toast));
        }
        return mAgreeChecbox.isChecked();
    }

    // 显示短信验证码登录Fragment
    private void showSmsCodeFragment() {
        // 用hide() show()无法控制Fragment的生命周期，无法控制埋点，所以用remove()
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        if (mAccountFragment != null) {
            transaction.remove(mAccountFragment);
        }

        mSmsCodefragment = new LoginSmsCodeFragment();
        mSmsCodefragment.setArguments(getInitFragmentArg());
        transaction.add(R.id.activity_login_framelayout, mSmsCodefragment);
        transaction.commit();

        mAccountLoginLinkTv.setVisibility(View.VISIBLE);
        mSmsCodeLoginLinkTv.setVisibility(View.GONE);
        mForgetPasswordTv.setVisibility(View.GONE);
    }

    // 显示账号密码登录Fragment
    private void showAccountFragment() {
        // 用hide() show()无法控制Fragment的生命周期，无法控制埋点，所以用remove()
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        if (mSmsCodefragment != null) {
            transaction.remove(mSmsCodefragment);
        }

        mAccountFragment = new LoginAccountFragment();
        mAccountFragment.setArguments(getInitFragmentArg());
        transaction.add(R.id.activity_login_framelayout, mAccountFragment);
        transaction.commit();

        mAccountLoginLinkTv.setVisibility(View.GONE);
        mSmsCodeLoginLinkTv.setVisibility(View.VISIBLE);
        mForgetPasswordTv.setVisibility(View.VISIBLE);
    }

    /**
     * 获取初始化登录fragment的参数
     */
    private Bundle getInitFragmentArg() {
        Bundle bundle = new Bundle();
        bundle.putBoolean(BUNDLE_KEY_AGREE_CHECK, mAgreeChecbox.isChecked());
        return bundle;
    }

}
