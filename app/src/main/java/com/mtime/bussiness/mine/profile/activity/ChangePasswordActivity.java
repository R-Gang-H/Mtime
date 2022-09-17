package com.mtime.bussiness.mine.profile.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.kk.taurus.uiframe.d.BaseState;
import com.kk.taurus.uiframe.v.BaseErrorHolder;
import com.kk.taurus.uiframe.v.BaseStateContainer;
import com.kk.taurus.uiframe.v.ContentHolder;
import com.kk.taurus.uiframe.v.NoTitleBarContainer;
import com.mtime.base.network.NetworkException;
import com.mtime.base.network.NetworkManager;
import com.mtime.base.utils.MToastUtils;
import com.mtime.bussiness.mine.api.MineApi;
import com.mtime.bussiness.mine.login.bean.SmsRegetPasswordVeryCode;
import com.mtime.bussiness.mine.login.holder.ChangePasswordHolder;
import com.mtime.bussiness.mine.login.widget.LoginSmsCodeView;
import com.mtime.bussiness.mine.profile.bean.RegetPasswordVeryCodeBean;
import com.mtime.frame.BaseFrameUIActivity;
import com.mtime.util.JumpUtil;

/**
 * @author vivian.wei
 * @date 2019/8/22
 * @desc 修改密码_发送短信验证码页
 */
public class ChangePasswordActivity extends BaseFrameUIActivity<Void, ChangePasswordHolder>
        implements LoginSmsCodeView.ILoginSmsCodeViewClickListener {

    public static final int HOLDER_EVENT_CODE_LOGIN = 1111;

    private MineApi mMineApi;

    @Override
    protected BaseStateContainer getStateContainer() {
        return new NoTitleBarContainer(this, this, this);
    }

    @Override
    public ContentHolder onBindContentHolder() {
        return new ChangePasswordHolder(this, this);
    }

    @Override
    public BaseErrorHolder onBindErrorHolder() {
        return super.onBindErrorHolder();
    }

    @Override
    protected void onParseIntent() {
        super.onParseIntent();
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
            case HOLDER_EVENT_CODE_LOGIN:
                // 跳转登录密码修改页
                JumpUtil.startChangePasswordLoginActivity(this, assemble().toString());
                finish();
                break;
            default:
                break;
        }
    }

    // 兼容物理返回键
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    // 点击获取验证码按钮
    @Override
    public void onSendSmsBtnClick(String mobile, String imgCodeId, String imgCode) {
        setPageState(BaseState.LOADING);

        // 手机找回密码发送校验码接口
        mMineApi.forgetPwdSendCode(mobile, 1, imgCodeId, imgCode,
                new NetworkManager.NetworkListener<SmsRegetPasswordVeryCode>() {
                    @Override
                    public void onSuccess(SmsRegetPasswordVeryCode bean, String showMsg) {
                        setPageState(BaseState.SUCCESS);

                        // 0 成功，-1验证码发送失败　-3 账号不存在　 -2 图片验证错误
                        if (0 == bean.getBizCode()) {
                            MToastUtils.showShortToast(bean.getMessage());
                            getUserContentHolder().sendSmsCodeSuccess(bean.getSmsCodeId());
                        } else if (-2 == bean.getBizCode()) {
                            // 弹出图片验证码
                            getUserContentHolder().showImgVCodeDlg(bean.getImgCodeId(), bean.getImgCodeUrl());
                        } else {
                            MToastUtils.showShortToast(bean.getMessage());
                        }
                    }

                    @Override
                    public void onFailure(NetworkException<SmsRegetPasswordVeryCode> exception, String showMsg) {
                        setPageState(BaseState.SUCCESS);
                        MToastUtils.showShortToast("发送验证码失败:" + showMsg);
                    }
                });
    }

    // 点击确定按钮
    @Override
    public void onLoginBtnClick(String mobile, String smsCode, String smsCodeId) {
        setPageState(BaseState.LOADING);
        mMineApi.regetPasswordVerycode(mobile, smsCode, smsCodeId, 1,
                new NetworkManager.NetworkListener<RegetPasswordVeryCodeBean>() {
                    @Override
                    public void onSuccess(RegetPasswordVeryCodeBean bean, String showMsg) {
                        setPageState(BaseState.SUCCESS);

                        // 成功-0, 验证失败--1；服务异常 -2
                        if (0 == bean.getBizCode()) {
                            // 设置密码页
                            JumpUtil.startSetPasswordActivity(ChangePasswordActivity.this, assemble().toString(), bean.getToken());
                            finish();
                        } else {
                            MToastUtils.showShortToast(bean.getMessage());
                        }
                    }

                    @Override
                    public void onFailure(NetworkException<RegetPasswordVeryCodeBean> exception, String showMsg) {
                        setPageState(BaseState.SUCCESS);
                        MToastUtils.showShortToast("验证失败:" + showMsg);
                    }
                });
    }

    public static void launch(Context context, String refer) {
        Intent launcher = new Intent(context, ChangePasswordActivity.class);
        dealRefer(context, refer, launcher);
        context.startActivity(launcher);
    }

}
