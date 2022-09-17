package com.mtime.bussiness.mine.profile.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.kk.taurus.uiframe.d.BaseState;
import com.kk.taurus.uiframe.v.BaseErrorHolder;
import com.kk.taurus.uiframe.v.BaseStateContainer;
import com.kk.taurus.uiframe.v.ContentHolder;
import com.kk.taurus.uiframe.v.NoTitleBarContainer;
import com.kotlin.android.user.UserManager;
import com.kotlin.android.app.data.entity.user.User;
import com.mtime.base.network.NetworkException;
import com.mtime.base.network.NetworkManager;
import com.mtime.base.utils.MToastUtils;
import com.mtime.bussiness.mine.api.MineApi;
import com.mtime.bussiness.mine.login.holder.BindPhoneWithLoginHolder;
import com.mtime.bussiness.mine.login.widget.LoginSmsCodeView;
import com.mtime.bussiness.mine.profile.bean.BindMobileResultBean;
import com.mtime.bussiness.ticket.movie.bean.SmsVeryCodeBean;
import com.mtime.constant.Constants;
import com.mtime.frame.App;
import com.mtime.frame.BaseFrameUIActivity;
import com.mtime.util.MtimeUtils;

/**
 * @author vivian.wei
 * @date 2019/8/19
 * @desc 绑定手机号页：登录后从其他页面跳转过来（非登录后直接绑定页）
 */
public class BindPhoneActivity extends BaseFrameUIActivity<Void, BindPhoneWithLoginHolder>
        implements LoginSmsCodeView.ILoginSmsCodeViewClickListener {

    private static final int PARAM_EXIST_MOBILE_ALLOW = 1;
    // 获取用户信息参数：1:用户绑定手机号后，跳过缓存重新获取信息
    private static final int ACCOUNT_DETAIL_PARAM = 1;

    private MineApi mMineApi;

    // 账号密码登录/第三方授权登录
    private int mBindFrom = -1;   // 当前页面时绑定|重新绑定|重新绑定获取新的电话号码页面：三个页面长得一样，获取api不一样
    private int mBindtype = 0;    // 绑定类型  默认0:新绑定  1:更换绑定
    private String mTargetActivityId;

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
        mTargetActivityId = getIntent().getStringExtra(App.getInstance().KEY_TARGET_ACTIVITY_ID);
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
        mMineApi.checkBindMobile(mobile, mBindtype, imgCodeId, imgCode, "", PARAM_EXIST_MOBILE_ALLOW,
                new NetworkManager.NetworkListener<SmsVeryCodeBean>() {
                    @Override
                    public void onSuccess(SmsVeryCodeBean bean, String showMsg) {
                        setPageState(BaseState.SUCCESS);

                        //  0 成功，
                        // -1 验证码发送失败　-2 新绑定手机号码已经被占用　 -3 用户请求非法　-4 请求验证码次数超过限制、或者需要图片验证码校验
                        // -5 手机号在库中存在：（第三方账号已经注册） 或者 （第三方账号未注册,验证的手机号绑定过同类三方账号）
                        // -6 existMobileAllow=1:允许给既存手机号发短信。 仍然可以返回bizCode=-6, 但和bizCode=0一样，也正常发短信验证码，允许直接登录和绑定

                        if (0 == bean.getBizCode() || bean.getBizCode() == -6) {
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
        setPageState(BaseState.LOADING);

        // 绑定的手机号短信验证码校验
        mMineApi.verifyBindMobileSmsCode(mobile, smsCode, smsCodeId, 1 + "", new NetworkManager.NetworkListener<SmsVeryCodeBean>() {
            @Override
            public void onSuccess(SmsVeryCodeBean bean, String showMsg) {
                setPageState(BaseState.SUCCESS);
                MToastUtils.showShortToast(bean.getMessage());

                // 0 成功　-1短信验证码错误 -2 图片验证码错误
                if (bean.getBizCode() == 0) {
                    // 直接执行绑定
                    bindMobileWithNormal(mobile, smsCode, smsCodeId, "");
                }
            }

            @Override
            public void onFailure(NetworkException<SmsVeryCodeBean> exception, String showMsg) {
                setPageState(BaseState.SUCCESS);
                MToastUtils.showShortToast("验证手机失败:" + showMsg);
            }
        });
    }

    // 绑定手机号  (已经注册为时光网账号的用户绑定手机号)
    private void bindMobileWithNormal(String mobile, String smsCode, String smsCodeId, String token) {
        // 非强制绑定的之前已经进行了登录操作。只需要执行绑定即可
        setPageState(BaseState.LOADING);

        mMineApi.bindMobile(mobile, smsCode, smsCodeId, token, new NetworkManager.NetworkListener<BindMobileResultBean>() {
            @Override
            public void onSuccess(BindMobileResultBean result, String showMsg) {
                setPageState(BaseState.SUCCESS);

                MToastUtils.showShortToast(result.getMessage());
                if (0 == result.getBizCode()) {
                    // 更新用户信息
                    updateUserInfo();
                }
            }

            @Override
            public void onFailure(NetworkException<BindMobileResultBean> exception, String showMsg) {
                setPageState(BaseState.SUCCESS);
                MToastUtils.showShortToast("绑定手机失败:" + showMsg);
            }
        });
    }

    /**
     * 更新用户信息
     */
    private void updateUserInfo() {
        setPageState(BaseState.LOADING);
        mMineApi.getAccountDetail(ACCOUNT_DETAIL_PARAM, new NetworkManager.NetworkListener<User>() {
            @Override
            public void onSuccess(User result, String showMsg) {
                setPageState(BaseState.SUCCESS);
                if (result == null) {
                    return;
                }
                UserManager.Companion.getInstance().update(result);

                MtimeUtils.startActivityWithID(getApplicationContext(), mTargetActivityId);
                finish();
            }

            @Override
            public void onFailure(NetworkException<User> exception, String showMsg) {
                setPageState(BaseState.SUCCESS);
                MToastUtils.showShortToast("更新用户信息失败:" + showMsg);

                MtimeUtils.startActivityWithID(getApplicationContext(), mTargetActivityId);
                finish();
            }
        });
    }

    public static void launch(Context context, String refer, boolean hasPassword) {
        Intent intent = new Intent(context, BindPhoneActivity.class);
        intent.putExtra(Constants.BIND_HAD_PASSWORD, hasPassword);
        dealRefer(context, refer, intent);
        context.startActivity(intent);
    }

}
