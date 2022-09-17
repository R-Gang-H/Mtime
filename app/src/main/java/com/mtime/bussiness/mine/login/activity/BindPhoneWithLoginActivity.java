package com.mtime.bussiness.mine.login.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.kk.taurus.uiframe.d.BaseState;
import com.kk.taurus.uiframe.v.BaseErrorHolder;
import com.kk.taurus.uiframe.v.BaseStateContainer;
import com.kk.taurus.uiframe.v.ContentHolder;
import com.kk.taurus.uiframe.v.NoTitleBarContainer;
import com.kotlin.android.retrofit.cookie.CookieManager;
import com.kotlin.android.user.UserManager;
import com.kotlin.android.user.login.jguang.JLoginManager;
import com.mtime.R;
import com.mtime.account.UserUtil;
import com.mtime.base.imageload.ImageHelper;
import com.mtime.base.network.NetworkException;
import com.mtime.base.network.NetworkManager;
import com.mtime.base.utils.MScreenUtils;
import com.mtime.base.utils.MToastUtils;
import com.mtime.bussiness.mine.api.MineApi;
import com.mtime.bussiness.mine.login.bean.CapchaBean;
import com.mtime.bussiness.mine.login.bean.LoginBean;
import com.mtime.bussiness.mine.login.bean.UserItem;
import com.mtime.bussiness.mine.login.holder.BindPhoneWithLoginHolder;
import com.mtime.bussiness.mine.login.widget.LoginSmsCodeView;
import com.mtime.bussiness.ticket.movie.bean.SmsVeryCodeBean;
import com.mtime.constant.Constants;
import com.mtime.event.EventManager;
import com.mtime.frame.App;
import com.mtime.frame.BaseFrameUIActivity;
import com.mtime.util.CapchaDlg;
import com.mtime.util.MtimeUtils;

/**
 * @author vivian.wei
 * @date 2019/8/16
 * @desc 手机绑定页（登录绑定手机）（2019版第三方登录必须绑定手机号，不能跳过；账号登录可以跳过直接登录）
 */
public class BindPhoneWithLoginActivity extends BaseFrameUIActivity<Void, BindPhoneWithLoginHolder>
        implements LoginSmsCodeView.ILoginSmsCodeViewClickListener {

    private static final int PARAM_EXIST_MOBILE_ALLOW = 1;

    private MineApi mMineApi;

    private String mUserAccount;        // 账号
    private String mPassword;
    // 账号密码登录/第三方授权登录
    private int mBindFrom = -1;   // 当前页面时绑定|重新绑定|重新绑定获取新的电话号码页面：三个页面长得一样，获取api不一样
    private int mBindtype = 0;    // 绑定类型  默认0:新绑定  1:更换绑定
    private String mToken;
    private String mAccessToken;
    private String mWeixinCode;
    private String mQqExpires;
    private int mRequestCode;
    private String mTargetActivityId;

    private String mImgcode;
    private String mImgecodeid;

    @Override
    protected BaseStateContainer getStateContainer() {
        return new NoTitleBarContainer(this, this, this);
    }

    @Override
    public ContentHolder onBindContentHolder() {
        return new BindPhoneWithLoginHolder(this, this);
    }

    @Override
    public BaseErrorHolder onBindErrorHolder() {
        return super.onBindErrorHolder();
    }

    @Override
    protected void onParseIntent() {
        super.onParseIntent();

        mBindFrom = getIntent().getIntExtra(Constants.BIND_FROM, 1);
        if (3 == mBindFrom) {
            mBindtype = 1;  // 更换绑定
        }

        mToken = getIntent().getStringExtra(Constants.BIND_ACCESS_TOKEN);
        if (TextUtils.isEmpty(mToken)) {
            mToken = "";
        }

        mAccessToken = getIntent().getStringExtra(Constants.BIND_THIRD_ACCESS_TOKEN);
        if (TextUtils.isEmpty(mAccessToken)) {
            mAccessToken = "";
        }

        mWeixinCode = getIntent().getStringExtra(Constants.BIND_WEIXIN_CODE);
        if (TextUtils.isEmpty(mWeixinCode)) {
            mWeixinCode = "";
        }

        mQqExpires = getIntent().getStringExtra(Constants.BIND_QQEXPIRES);
        if (TextUtils.isEmpty(mQqExpires)) {
            mQqExpires = "";
        }

        mUserAccount = getIntent().getStringExtra(Constants.BIND_LOGIN_ACCOUNT);
        if (TextUtils.isEmpty(mUserAccount)) {
            mUserAccount = "";
        }

        mPassword = getIntent().getStringExtra(Constants.BIND_LOGIN_PASSWORD);
        if (TextUtils.isEmpty(mPassword)) {
            mPassword = "";
        }

        mTargetActivityId = getIntent().getStringExtra(App.getInstance().KEY_TARGET_ACTIVITY_ID);
        mRequestCode = getIntent().getIntExtra(App.getInstance().KEY_REQUEST_CODE, -1);
    }

    @Override
    protected void onInit(Bundle savedInstanceState) {
        super.onInit(savedInstanceState);

        // 埋点
        mBaseStatisticHelper.setPageLabel("bindPhone");

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
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onHolderEvent(int eventCode, Bundle bundle) {
        super.onHolderEvent(eventCode, bundle);

        switch (eventCode) {
            default:
                break;
        }
    }

    // 点击获取验证码按钮
    @Override
    public void onSendSmsBtnClick(String mobile, String imgCodeId, String imgCode) {
        setPageState(BaseState.LOADING);

        // 检查绑定手机号并发送短信验证码
        // token: 使用接口返回的第三方授权token，不是第三方返回的授权token
        mMineApi.checkBindMobile(mobile, mBindtype, imgCodeId, imgCode, mToken, PARAM_EXIST_MOBILE_ALLOW,
                new NetworkManager.NetworkListener<SmsVeryCodeBean>() {
            @Override
            public void onSuccess(SmsVeryCodeBean bean, String showMsg) {
                setPageState(BaseState.SUCCESS);

                //  0 成功，
                // -1 验证码发送失败　-2 新绑定手机号码已经被占用　 -3 用户请求非法　-4 请求验证码次数超过限制、或者需要图片验证码校验
                // -5 手机号在库中存在：（第三方账号已经注册） 或者 （第三方账号未注册,验证的手机号绑定过同类三方账号）
                // -6 existMobileAllow=1:允许给既存手机号发短信。 仍然可以返回bizCode=-6, 但和bizCode=0一样，也正常发短信验证码，允许直接登录和绑定

                if (bean.getBizCode() == 0 || bean.getBizCode() == -6) {
                    // 发送短信成功
                    MToastUtils.showShortToast(bean.getMessage());
                    getUserContentHolder().sendSmsCodeSuccess(bean.getSmsCodeId());
                } else if (-4 == bean.getBizCode()) {
                    // 显示图片验证码
                    getUserContentHolder().showImgVCodeDlg(bean.getImgCodeId(), bean.getImgCodeUrl());
                } else {
                    MToastUtils.showShortToast(bean.getMessage());
                }
            }

            @Override
            public void onFailure(NetworkException<SmsVeryCodeBean> exception, String showMsg) {
                setPageState(BaseState.SUCCESS);
                MToastUtils.showShortToast(showMsg);
            }
        });
    }

    // 点击确定按钮
    @Override
    public void onLoginBtnClick(String mobile, String smsCode, String smsCodeId) {
        mImgcode = "";
        mImgecodeid = "";
        // 调用登录接口进行绑定  /user/login.api
        bindMobileWithEmailAccount(mobile, mImgcode, mImgecodeid, smsCode, smsCodeId);
    }

    // 邮箱绑定手机，强制绑定
    private void bindMobileWithEmailAccount(String mobile, String imgCode, String imgCodeId, String smsCode, String smsCodeId) {
        setPageState(BaseState.LOADING);

        // 强制绑定的需要使用之前的登录接口进行绑定 mToken是https://comm-api-m.mtime.cn/user/oauth/login.api返回的字段token值
        mMineApi.userLogin(mUserAccount, mPassword, imgCode, imgCodeId, mobile, smsCode, smsCodeId, mToken,
                new NetworkManager.NetworkListener<LoginBean>() {
                    @Override
                    public void onSuccess(LoginBean bean, String showMsg) {
                        setPageState(BaseState.SUCCESS);

                        if (bean.getStatus() == 1) {
                            JLoginManager.Companion.getInstance().dismissLoginAuthActivity(null, null);
                            // 1 登录成功
                            // 更新数据
                            UserItem userItem = bean.getUser();
                            userItem.setMobile(mobile);
//                            AccountManager.updateAccountInfo(userItem, bean.isHasPassword());
                            UserManager.Companion.getInstance().update(UserUtil.toItemUser(userItem), bean.isHasPassword());
                            // 发送事件
                            EventManager.getInstance().sendLoginEvent(mTargetActivityId);

                            MtimeUtils.startActivityWithID(getApplicationContext(), mTargetActivityId);
                            if (-1 != mRequestCode) {
                                setResult(App.STATUS_LOGIN);
                            }
                            finish();
                            return;
                        }

                        //登录失败，则清理cookie.
//                        NetworkManager.getInstance().mCookieJarManager.clear();
                        CookieManager.Companion.getInstance().clear();

                        // 状态值4 验证出错时，返回新的验证Id
                        if (bean.getStatus() == 4) {
                            // 显示图片验证码弹窗
                            requestBindCapcha(mobile, bean.getCodeId(), bean.getCodeUrl(), smsCode, smsCodeId);
                        } else {
                            MToastUtils.showShortToast(bean.getMsg());
                        }
                    }

                    @Override
                    public void onFailure(NetworkException<LoginBean> exception, String showMsg) {
                        setPageState(BaseState.SUCCESS);
                        MToastUtils.showShortToast(showMsg);
                    }
                });
    }

    // （邮箱绑定调用登录接口）显示图片验证码弹窗
    private void requestBindCapcha(String mobile, String imgcodeId, String imgcodeUrl, String smsCode, String smsCodeId) {
        mImgecodeid = imgcodeId;
        CapchaDlg capchaDlg = new CapchaDlg(this, CapchaDlg.TYPE_OK_CANCEL, R.layout.dialog_capcha_login);
        capchaDlg.setBackgroundDimAmount(0.12f);
        capchaDlg.setCanceledOnTouchOutside(false);

        capchaDlg.setBtnOKListener(new View.OnClickListener() {

            @Override
            public void onClick(final View v) {
                mImgcode = capchaDlg.getEditView().getText().toString();
                if (TextUtils.isEmpty(mImgcode)) {
                    MToastUtils.showShortToast("请输入图片验证码");
                    return;
                }
                bindMobileWithEmailAccount(mobile, mImgcode, mImgecodeid, smsCode, smsCodeId);
                capchaDlg.dismiss();
            }
        });

        capchaDlg.setBtnCancelListener(new View.OnClickListener() {

            @Override
            public void onClick(final View v) {
                capchaDlg.dismiss();
            }
        });

        capchaDlg.setCapchaTextListener(new View.OnClickListener() {

            @Override
            public void onClick(final View v) {
                setPageState(BaseState.LOADING);
                // 刷新图片验证码
                mMineApi.getImageVerifyCode(new NetworkManager.NetworkListener<CapchaBean>() {
                    @Override
                    public void onSuccess(CapchaBean result, String showMsg) {
                        setPageState(BaseState.SUCCESS);
                        mImgecodeid = result.getCodeId();
                        ImageHelper.with(BindPhoneWithLoginActivity.this)
                                .override(MScreenUtils.dp2px(75), MScreenUtils.dp2px(30))
                                .view(capchaDlg.getImageView())
                                .load(result.getUrl())
                                .asGif()
                                .showload();
                    }

                    @Override
                    public void onFailure(NetworkException<CapchaBean> exception, String showMsg) {
                        setPageState(BaseState.SUCCESS);
                        MToastUtils.showShortToast("获取图片验证码失败：" + showMsg);
                    }
                });
            }
        });

        capchaDlg.show();
        ImageHelper.with(this)
                .override(MScreenUtils.dp2px(75), MScreenUtils.dp2px(30))
                .view(capchaDlg.getImageView())
                .load(imgcodeUrl)
                .asGif()
                .showload();
    }

    /**
     * 自己定义refer
     *
     * @param context
     * @param refer
     */
    public static void launch(Context context, String refer, int bindFrom, String account, String password) {
        Intent intent = new Intent(context, BindPhoneWithLoginActivity.class);
        intent.putExtra(Constants.BIND_FROM, bindFrom);
        intent.putExtra(Constants.BIND_LOGIN_ACCOUNT, account);
        intent.putExtra(Constants.BIND_LOGIN_PASSWORD, password);
        dealRefer(context, refer, intent);
        context.startActivity(intent);
    }

    public static void launch(Context context, String refer, int bindFrom, String accessToken, String thirdAccessToken,
                              String wechatCode, String platformId, String expires, String targetActivityId,
                              boolean hasPassword, boolean registerStatus, int requestCode) {
        Intent intent = new Intent(context, BindPhoneWithLoginActivity.class);
        intent.putExtra(Constants.BIND_FROM, bindFrom);
        // 这里的token是接口返回的token,不是第三方返回的token.
        intent.putExtra(Constants.BIND_ACCESS_TOKEN, accessToken);
        // 这个才是第三方登录后返回的accesstoken.
        intent.putExtra(Constants.BIND_THIRD_ACCESS_TOKEN, thirdAccessToken);
        intent.putExtra(Constants.BIND_WEIXIN_CODE, wechatCode);
        intent.putExtra(Constants.BIND_PLATFORM, platformId);
        intent.putExtra(Constants.BIND_QQEXPIRES, expires);
        intent.putExtra(App.getInstance().KEY_TARGET_ACTIVITY_ID, targetActivityId);
        intent.putExtra(Constants.BIND_HAD_PASSWORD, hasPassword);
        // 修改密码页会用到
        intent.putExtra(Constants.BIND_REGISTER_STATUS, registerStatus);
        intent.putExtra(App.getInstance().KEY_REQUEST_CODE, requestCode);
        dealRefer(context, refer, intent);
        if (context instanceof Activity) {
            ((Activity) context).startActivityForResult(intent, requestCode);
        } else {
            context.startActivity(intent);
        }
    }
}
