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
import com.mtime.base.network.NetworkException;
import com.mtime.base.network.NetworkManager;
import com.mtime.base.utils.MToastUtils;
import com.mtime.bussiness.mine.api.MineApi;
import com.mtime.bussiness.mine.login.holder.BindPhoneWithLoginHolder;
import com.mtime.bussiness.mine.login.widget.LoginSmsCodeView;
import com.mtime.bussiness.ticket.movie.bean.SmsVeryCodeBean;
import com.mtime.frame.App;
import com.mtime.frame.BaseFrameUIActivity;
import com.mtime.util.JumpUtil;

/**
 * @author vivian.wei
 * @date 2019/8/19
 * @desc 身份验证页（个人资料_绑定手机_更改手机号码_验证码输入页面 进入）
 */
public class SecuritycodeActivity extends BaseFrameUIActivity<Void, BindPhoneWithLoginHolder>
        implements LoginSmsCodeView.ILoginSmsCodeViewClickListener {

    private static final int PARAM_EXIST_MOBILE_ALLOW = 1;

    private String mPhone;
    private int mBindtype = -1;    // 绑定类型  默认0:新绑定  1:更换绑定
    private MineApi mMineApi;

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

        mPhone = getIntent().getStringExtra(App.getInstance().KEY_BINDPHONE);
        mBindtype = getIntent().getIntExtra(App.getInstance().KEY_BIND_TYPE, 1);
    }

    @Override
    protected void onInit(Bundle savedInstanceState) {
        super.onInit(savedInstanceState);

        // 埋点
        mBaseStatisticHelper.setPageLabel("verifyCodeInput");

        if (null == mMineApi) {
            mMineApi = new MineApi();
        }

        getUserContentHolder().setTitle("身份验证");
        // 隐藏标题下方的提示
        getUserContentHolder().hideTopTip();
        if (!TextUtils.isEmpty(mPhone)) {
            // 设置默认手机号
            getUserContentHolder().setDefaultMobile(mPhone);
            // 设置手机文本框不能编辑
            getUserContentHolder().enablePhoneEt(false);
        }

        // 进入页面就请求接口
        onSendSmsBtnClick(mPhone, "", "");
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
        mMineApi.verifyBindMobileSmsCode(mobile, smsCode, smsCodeId, 0 + "", new NetworkManager.NetworkListener<SmsVeryCodeBean>() {
            @Override
            public void onSuccess(SmsVeryCodeBean bean, String showMsg) {
                setPageState(BaseState.SUCCESS);
                MToastUtils.showShortToast(bean.getMessage());

                // 0 成功　-1短信验证码错误 -2 图片验证码错误
                if (bean.getBizCode() == 0) {
                    // 绑定手机号页：登录后从其他页面跳转过来（非登录后直接绑定页）
                    boolean hasPassword = UserManager.Companion.getInstance().getHasPassword();
                    JumpUtil.startBindPhoneActivity(SecuritycodeActivity.this, assemble().toString(), hasPassword);
                    finish();
                }
            }

            @Override
            public void onFailure(NetworkException<SmsVeryCodeBean> exception, String showMsg) {
                setPageState(BaseState.SUCCESS);
                MToastUtils.showShortToast(showMsg);
            }
        });
    }

    public static void launch(Context context, String refer, String bindMobie, int bindType) {
        Intent intent = new Intent(context, SecuritycodeActivity.class);
        intent.putExtra(App.getInstance().KEY_BINDPHONE, bindMobie);
        intent.putExtra(App.getInstance().KEY_BIND_TYPE, bindType);
        dealRefer(context, refer, intent);
        context.startActivity(intent);
    }

}
