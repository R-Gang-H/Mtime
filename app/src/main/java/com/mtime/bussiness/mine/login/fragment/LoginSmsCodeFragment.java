package com.mtime.bussiness.mine.login.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;


import com.jeremyliao.liveeventbus.LiveEventBus;
import com.kk.taurus.uiframe.d.BaseState;
import com.kk.taurus.uiframe.v.BaseErrorHolder;
import com.kk.taurus.uiframe.v.BaseLoadingHolder;
import com.kk.taurus.uiframe.v.ContentHolder;
import com.kotlin.android.retrofit.cookie.CookieManager;
import com.kotlin.android.app.router.liveevent.event.LoginAgreeState;
import com.kotlin.android.user.UserManager;
import com.kotlin.android.user.login.jguang.JLoginManager;
import com.mtime.R;
import com.mtime.account.UserUtil;
import com.mtime.base.network.NetworkException;
import com.mtime.base.network.NetworkManager;
import com.mtime.base.utils.MToastUtils;
import com.mtime.bussiness.mine.api.MineApi;
import com.mtime.bussiness.mine.login.bean.LoginBean;
import com.mtime.bussiness.mine.login.bean.SmsCodeBean;
import com.mtime.bussiness.mine.login.holder.LoginSmsCodeHolder;
import com.mtime.bussiness.mine.login.widget.LoginSmsCodeView;
import com.mtime.event.EventManager;
import com.mtime.frame.BaseFrameUIActivity;
import com.mtime.frame.BaseFrameUIFragment;

import static com.kotlin.android.router.liveevent.EventKeyExtKt.LOGIN_AGREE_STATE;
import static com.mtime.constant.Constants.BUNDLE_KEY_AGREE_CHECK;

/**
 * @author vivian.wei
 * @date 2019/8/8
 * @desc 短信验证码登录
 * 埋点：http://wiki.inc-mtime.com/pages/viewpage.action?pageId=247758852
 */
public class LoginSmsCodeFragment extends BaseFrameUIFragment<Void, LoginSmsCodeHolder>
    implements LoginSmsCodeView.ILoginSmsCodeViewClickListener, LoginSmsCodeView.OnEditTextChangeListener {

    private MineApi mMineApi;
    private boolean mAgreeChecked;  // 是否选中底部同意条款

    @Override
    public ContentHolder onBindContentHolder() {
        return new LoginSmsCodeHolder(mContext, this);
    }

    @Override
    public BaseErrorHolder onBindErrorHolder() {
        return super.onBindErrorHolder();
    }

    @Override
    public BaseLoadingHolder onBindLoadingHolder() {
        return super.onBindLoadingHolder();
    }

    @Override
    protected void onInit(Bundle savedInstanceState) {
        super.onInit(savedInstanceState);

        mAgreeChecked = getArguments().getBoolean(BUNDLE_KEY_AGREE_CHECK);

        if (null == mMineApi) {
            mMineApi = new MineApi();
        }

        // Fragment埋点
        setPageLabel("register");
        mBaseStatisticHelper.setLastPageRefer(((BaseFrameUIActivity) getActivity()).getRefer());
        // LoginActivity埋点
        ((BaseFrameUIActivity) getActivity()).setPageLabel("register");
        ((BaseFrameUIActivity) getActivity()).setSubmit(true);

        // 文本框内容发生改变
        getUserContentHolder().setOnTextChangeListener(this);
        agreeEventObserve();
    }

    @Override
    protected void onErrorRetry() {
        super.onErrorRetry();
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
    protected boolean openEventBus() {
        return true;
    }

    // 点击获取验证码按钮
    @Override
    public void onSendSmsBtnClick(String phone, String imgCodeId, String imgCode) {
        //埋点
        mBaseStatisticHelper.assemble1(
                "getCode", null,
                "click", null,
                "", null, null).submit();

        setPageState(BaseState.LOADING);

        // 发送短信验证码（不检查手机号是否已经注册，短信验证码登录页使用）
        mMineApi.sendLoginSmsCode(phone, imgCodeId, imgCode, new NetworkManager.NetworkListener<SmsCodeBean>() {
            @Override
            public void onSuccess(SmsCodeBean result, String showMsg) {
                setPageState(BaseState.SUCCESS);

                // 0成功，2非法手机号，3图片验证码不正确 4.其他异常、5 频繁下发短信  （没有返回值1,注册时才有）

                // 0 成功
                if (0 == result.getBizCode()) {
                    MToastUtils.showShortToast(result.getBizMsg());
                    getUserContentHolder().sendSmsCodeSuccess(result.getSmsCodeId());
                    return;
                }

                // 3 图片验证码不正确 （会返回新的图片验证码）
                if (3 == result.getBizCode() && !TextUtils.isEmpty(result.getImgCode().getCodeId())) {
                    // 直接显示返回的图片验证码
                    getUserContentHolder().showImgVCodeDlg(result.getImgCode().getCodeId(), result.getImgCode().getCodeUrl());
                    return;
                }

                // 2非法手机号 4其他异常 5频繁下发短信
                MToastUtils.showShortToast(result.getBizMsg());
            }

            @Override
            public void onFailure(NetworkException<SmsCodeBean> exception, String showMsg) {
                setPageState(BaseState.SUCCESS);
                MToastUtils.showShortToast("发送短信验证码失败：" + showMsg);
            }
        });
    }

    // 点击登录按钮
    @Override
    public void onLoginBtnClick(String mobile, String smsCode, String smsCodeId) {
        //埋点
        mBaseStatisticHelper.assemble1(
                "registerIn", null,
                "click", null,
                "", null, null).submit();

        if(mAgreeChecked) {
            setPageState(BaseState.LOADING);

            mMineApi.userLogin("", "", "", "", mobile, smsCode, smsCodeId, "",
                    new NetworkManager.NetworkListener<LoginBean>() {
                        @Override
                        public void onSuccess(LoginBean bean, String showMsg) {
                            setPageState(BaseState.SUCCESS);

                            // 1 登录成功、2 账号或密码有误，请重试、3 该账号已被禁止登录、4 请输入图片验证码、5 强制绑定手机、6 绑定手机出错
                            if (1 == bean.getStatus()) {
                                // 登录成功
                                MToastUtils.showShortToast(bean.getMsg());
                                JLoginManager.Companion.getInstance().dismissLoginAuthActivity(null, null);
                                // 理论上这里应该是必须有密码的
//                            AccountManager.updateAccountInfo(bean.getUser(), bean.isHasPassword());
                                UserManager.Companion.getInstance().update(UserUtil.toItemUser(bean.getUser()), bean.isHasPassword());
                                // 发送事件
                                EventManager.getInstance().sendLoginEvent(null);
                                return;
                            }
                            //登录失败，则清理cookie
//                        NetworkManager.getInstance().mCookieJarManager.clear();
                            CookieManager.Companion.getInstance().clear();
                            MToastUtils.showShortToast(bean.getMsg());
                        }

                        @Override
                        public void onFailure(NetworkException<LoginBean> exception, String showMsg) {
                            setPageState(BaseState.SUCCESS);
                            MToastUtils.showShortToast(showMsg);
                        }
                    });
        } else {
            MToastUtils.showShortToast(getString(R.string.checkbox_hint_toast));
        }
    }

    // 文本框内容发生改变
    @Override
    public void onEditTextChange(View view) {
        //埋点
        String firstRegion = "";
        if(view.getId() == R.id.login_sms_code_view_phone_number_et) {
            firstRegion = "enterNumber";
        } else if(view.getId() == R.id.login_sms_code_view_code_et) {
            firstRegion = "enterCode";
        }
        mBaseStatisticHelper.assemble1(
                firstRegion, null,
                "enter", null,
                "", null, null).submit();
    }

    /**
     * 监听底部条款选中状态
     */
    private void agreeEventObserve() {
        LiveEventBus.get(LOGIN_AGREE_STATE, LoginAgreeState.class).observe(this, state -> {
            mAgreeChecked = state.getChecked();
        });
    }
}
