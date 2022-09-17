package com.mtime.bussiness.mine.profile.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.kk.taurus.uiframe.d.BaseState;
import com.kk.taurus.uiframe.v.BaseErrorHolder;
import com.kk.taurus.uiframe.v.BaseStateContainer;
import com.kk.taurus.uiframe.v.ContentHolder;
import com.kk.taurus.uiframe.v.NoTitleBarContainer;
import com.kotlin.android.user.UserManager;
import com.kotlin.android.user.login.jguang.JLoginManager;
import com.mtime.account.UserUtil;
import com.mtime.base.network.NetworkException;
import com.mtime.base.network.NetworkManager;
import com.mtime.base.utils.MToastUtils;
import com.mtime.bussiness.mine.api.MineApi;
import com.mtime.bussiness.mine.login.bean.RegisterBean;
import com.mtime.bussiness.mine.login.bean.ThirdLoginBean;
import com.mtime.bussiness.mine.login.bean.UserItem;
import com.mtime.bussiness.mine.login.holder.SetPasswordHolder;
import com.mtime.bussiness.mine.profile.bean.RegetPasswordVeryCodeBean;
import com.mtime.bussiness.mine.profile.bean.RegetPasswordWithLoginBean;
import com.mtime.constant.Constants;
import com.mtime.frame.App;
import com.mtime.frame.BaseFrameUIActivity;
import com.mtime.util.MtimeUtils;
import com.mtime.util.ToolsUtils;

/**
 * @author vivian.wei
 * @date 2019/8/21
 * @desc 设置密码页
 * 入口：
 * 1.找回密码->手机/邮箱：验证码验证成功后跳转设置密码页
 * 2.个人中心-修改密码页：优先进入短信验证码修改密码页->短信验证码通过后跳转设置密码页
 */
public class SetPasswordActivity extends BaseFrameUIActivity<Void, SetPasswordHolder> {

    public static final int HOLDER_EVENT_OK_BTN_CLICK = 1001;

    private String mToken;      // 验证的凭证, 空-修改密码，否则-设置密码
    private String mMobile;     // 绑定手机号, 空-正常的设置密码， 否则-
    private boolean mIsLoginBind;
    private boolean mHasRegister;

    private String mMobileToken;
    private String mAccessToken;
    private String mQqExpires;
    private String mPlatformId ;
    private String mWeixinCode;
    private String mAuthtoken;

    private MineApi mMineApi;

    @Override
    protected BaseStateContainer getStateContainer() {
        return new NoTitleBarContainer(this, this, this);
    }

    @Override
    public ContentHolder onBindContentHolder() {
        return new SetPasswordHolder(this, false);
    }

    @Override
    public BaseErrorHolder onBindErrorHolder() {
        return super.onBindErrorHolder();
    }

    @Override
    protected void onParseIntent() {
        super.onParseIntent();

        mToken = getIntent().getStringExtra(App.getInstance().KEY_RETRIEVE_PASSWORD_BY_PHONE_TOKEN);
        mMobile = getIntent().getStringExtra(Constants.BIND_MOBILE_NUMBER);
        mIsLoginBind = getIntent().getBooleanExtra(Constants.BIND_MOBILE_WITH_LOGIN, true);
        mHasRegister = getIntent().getBooleanExtra(Constants.BIND_REGISTER_STATUS, true);

        // 第三方登录参数
        mMobileToken = getIntent().getStringExtra(Constants.BIND_MOBILE_TOKEN);
        mAccessToken = getIntent().getStringExtra(Constants.BIND_THIRD_ACCESS_TOKEN);
        mQqExpires = getIntent().getStringExtra(Constants.BIND_QQEXPIRES);
        mPlatformId = getIntent().getStringExtra(Constants.BIND_PLATFORM);
        mWeixinCode = getIntent().getStringExtra(Constants.BIND_WEIXIN_CODE);
        mAuthtoken = getIntent().getStringExtra(Constants.BIND_MOBILE_WITH_THIRD_OAUTHTOKEN);
    }

    @Override
    protected void onInit(Bundle savedInstanceState) {
        super.onInit(savedInstanceState);

        // 埋点
        mBaseStatisticHelper.setPageLabel("changePassword");

        if (null == mMineApi) {
            mMineApi = new MineApi();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (null != mMineApi) {
            mMineApi.cancel();
        }
    }

    @Override
    public void onHolderEvent(int eventCode, Bundle bundle) {
        super.onHolderEvent(eventCode, bundle);

        switch (eventCode) {
            case HOLDER_EVENT_OK_BTN_CLICK:
                // 点击确定按钮
                clickOkBtn();
            default:
                break;
        }
    }

    // 兼容物理返回键
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    // 点击确定按钮
    private void clickOkBtn() {
        String newPwd = getUserContentHolder().getNewPwd();
        String newPwdConfirm = getUserContentHolder().getNewPwdConfirm();

        // 新密码
        if (TextUtils.isEmpty(newPwd) || 0 == newPwd.length()) {
            MToastUtils.showShortToast("请输入您的新密码");
            return;
        }

        // 这里需要有逻辑对密码进行validation
        if (newPwd.length() < 6 || newPwd.length() > 20) {
            MToastUtils.showShortToast("新密码长度要在6-20个字符以内");
            return;
        }

        if (MtimeUtils.isChinese(newPwd)) {
            MToastUtils.showShortToast("新密码包含中文字符");
            return;
        }

        // 确认密码
        if (TextUtils.isEmpty(newPwdConfirm) || newPwdConfirm.length() == 0) {
            MToastUtils.showShortToast("请输入确认密码");
            return;
        }

        if (!newPwd.equals(newPwdConfirm)) {
            MToastUtils.showShortToast("新密码俩次输入不一致，请检查");
            return;
        }

        if (TextUtils.isEmpty(mMobile)) {
            // 正常保存密码并登录，更新用户信息
            setPageState(BaseState.LOADING);

            mMineApi.saveNewPwd(mToken, newPwd, newPwdConfirm, new NetworkManager.NetworkListener<RegetPasswordWithLoginBean>() {
                @Override
                public void onSuccess(RegetPasswordWithLoginBean bean, String showMsg) {
                    setPageState(BaseState.SUCCESS);

                    MToastUtils.showShortToast(bean.getMessage());
                    // 0成功 1失败  -1令牌无效
                    if (0 == bean.getBizCode()) {
                        //更新用户信息
                        updateUserInfo(bean.getUserInfo(), true);
                        finish();
                    }
                }

                @Override
                public void onFailure(NetworkException<RegetPasswordWithLoginBean> exception, String showMsg) {
                    setPageState(BaseState.SUCCESS);
                    MToastUtils.showShortToast("设置密码失败：" + showMsg);
                }
            });
        } else {
            if (mIsLoginBind) {
                // 如果是登录绑定的话
                // 第三方的设置密码接口
                if (mHasRegister) {
                    // 第三方授权登录
                    bindMobileWithThird(newPwd, newPwdConfirm);
                } else {
                    // 设置密码
                    setMobilePassword(newPwd, newPwdConfirm);
                }
            } else {
                // 登录后的第一次绑定手机号后设置密码接口
                setMobileAndPassword(newPwd, newPwdConfirm);
            }
        }
    }

    // 第三方授权登录
    private void bindMobileWithThird(String password, String confirmPassword) {
        setPageState(BaseState.LOADING);

        mMineApi.userOauthLogin(mAccessToken, mQqExpires, mPlatformId, mWeixinCode, mMobileToken, password, confirmPassword,
                new NetworkManager.NetworkListener<ThirdLoginBean>() {
            @Override
            public void onSuccess(ThirdLoginBean bean, String showMsg) {
                setPageState(BaseState.SUCCESS);

                if (1 == bean.getStatus()) {
                    updateUserInfo(bean.getUser(), bean.isHasPassword());
                    JLoginManager.Companion.getInstance().dismissLoginAuthActivity(null, null);
                    finish();
                } else {
                    MToastUtils.showShortToast("设置密码失败:" + bean.getMsg());
                }
            }

            @Override
            public void onFailure(NetworkException<ThirdLoginBean> exception, String showMsg) {
                setPageState(BaseState.SUCCESS);
                MToastUtils.showShortToast("设置密码失败:" + showMsg);
            }
        });
    }

    // 未注册用户第三方登录设置密码
    private void setMobilePassword(String password, String confirmPassword) {
        setPageState(BaseState.LOADING);

        mMineApi.oauthSetPassword(mAuthtoken, mMobileToken, password, confirmPassword, new NetworkManager.NetworkListener<RegisterBean>() {
                    @Override
                    public void onSuccess(RegisterBean bean, String showMsg) {
                        setPageState(BaseState.SUCCESS);

                        MToastUtils.showShortToast(bean.getBizMsg());
                        if (0 == bean.getBizCode()) {
                            updateUserInfo(bean.getUserInfo(), true);
                            finish();
                        }
                    }

                    @Override
                    public void onFailure(NetworkException<RegisterBean> exception, String showMsg) {
                        setPageState(BaseState.SUCCESS);
                        MToastUtils.showShortToast("设置密码失败:" + showMsg);
                    }
                });
    }

    // 登录后的第一次绑定手机号后设置密码接口
    private void setMobileAndPassword(String password, String confirmPassword) {
        setPageState(BaseState.LOADING);

        mMineApi.setPwdAndBindMobile(mMobile, password, confirmPassword, new NetworkManager.NetworkListener<RegetPasswordVeryCodeBean>() {
            @Override
            public void onSuccess(RegetPasswordVeryCodeBean bean, String showMsg) {
                setPageState(BaseState.SUCCESS);

                MToastUtils.showShortToast(bean.getMessage());
                if (0 == bean.getBizCode()) {
                    finish();
                }
            }

            @Override
            public void onFailure(NetworkException<RegetPasswordVeryCodeBean> exception, String showMsg) {
                setPageState(BaseState.SUCCESS);
                MToastUtils.showShortToast("设置密码失败:" + showMsg);
            }
        });
    }

    // 更新用户信息
    private void updateUserInfo( UserItem info, final boolean hasPassword) {
        UserManager.Companion.getInstance().update(UserUtil.toItemUser(info), hasPassword);
//        AccountManager.updateAccountInfo(info, hasPassword);

        ToolsUtils.syncFavorites();
        ToolsUtils.sendConfigMsg(getApplicationContext());

        String targetActivityId = getIntent().getStringExtra(App.getInstance().KEY_TARGET_ACTIVITY_ID);
        MtimeUtils.startActivityWithID(SetPasswordActivity.this, targetActivityId);

        setResult(App.STATUS_LOGIN);
    }

    // 启动
    public static void launch(Context context, String refer, String token) {
        Intent intent = new Intent(context, SetPasswordActivity.class);
        intent.putExtra(App.getInstance().KEY_RETRIEVE_PASSWORD_BY_PHONE_TOKEN, token);
        dealRefer(context, refer, intent);
        context.startActivity(intent);
    }

    // 启动
    public static void launch(Context context, String refer, String mobileToken, String mobileNumber,  boolean bindMobileWithLogin, String token) {
        Intent intent = new Intent(context, SetPasswordActivity.class);
        intent.putExtra(Constants.BIND_MOBILE_TOKEN, mobileToken);
        intent.putExtra(Constants.BIND_MOBILE_NUMBER, mobileNumber);
        intent.putExtra(Constants.BIND_MOBILE_WITH_LOGIN, bindMobileWithLogin);
        intent.putExtra(App.getInstance().KEY_RETRIEVE_PASSWORD_BY_PHONE_TOKEN, token);
        dealRefer(context, refer, intent);
        context.startActivity(intent);
    }

    // 启动
    public static void launch(Context context, String refer, String mobileToken, String mobileNumber,
                              boolean isLoginBind, String token, String thirdOauthToken, String accessToken) {
        Intent intent = new Intent(context, SetPasswordActivity.class);
        intent.putExtra(Constants.BIND_MOBILE_TOKEN, mobileToken);
        intent.putExtra(Constants.BIND_MOBILE_NUMBER, mobileNumber);
        intent.putExtra(Constants.BIND_MOBILE_WITH_LOGIN, isLoginBind);
        intent.putExtra(App.getInstance().KEY_RETRIEVE_PASSWORD_BY_PHONE_TOKEN, token);
        intent.putExtra(Constants.BIND_MOBILE_WITH_THIRD_OAUTHTOKEN, thirdOauthToken);
        intent.putExtra(Constants.BIND_THIRD_ACCESS_TOKEN, accessToken);
        dealRefer(context, refer, intent);
        context.startActivity(intent);
    }

}
