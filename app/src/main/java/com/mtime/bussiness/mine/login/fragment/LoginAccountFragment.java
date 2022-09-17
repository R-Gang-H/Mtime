package com.mtime.bussiness.mine.login.fragment;

import android.os.Bundle;
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
import com.mtime.base.imageload.ImageHelper;
import com.mtime.base.network.NetworkException;
import com.mtime.base.network.NetworkManager;
import com.mtime.base.utils.CollectionUtils;
import com.mtime.base.utils.MScreenUtils;
import com.mtime.base.utils.MToastUtils;
import com.mtime.bussiness.mine.api.MineApi;
import com.mtime.bussiness.mine.login.bean.CapchaBean;
import com.mtime.bussiness.mine.login.bean.EmailSuffix;
import com.mtime.bussiness.mine.login.bean.LoginBean;
import com.mtime.bussiness.mine.login.holder.LoginAccountHolder;
import com.mtime.constant.Constants;
import com.mtime.event.EventManager;
import com.mtime.frame.BaseFrameUIActivity;
import com.mtime.frame.BaseFrameUIFragment;
import com.mtime.util.CapchaDlg;
import com.mtime.util.Des;
import com.mtime.util.JumpUtil;

import java.util.Arrays;
import java.util.List;

import static com.kotlin.android.router.liveevent.EventKeyExtKt.LOGIN_AGREE_STATE;
import static com.mtime.constant.Constants.BUNDLE_KEY_AGREE_CHECK;

/**
 * @author vivian.wei
 * @date 2019/8/8
 * @desc 登录页_账号密码登录Fragment
 * 埋点：http://wiki.inc-mtime.com/pages/viewpage.action?pageId=247758862
 */
public class LoginAccountFragment extends BaseFrameUIFragment<List<String>, LoginAccountHolder> {

    public static final int HOLDER_EVENT_CODE_LOGIN = 1;
    public static final int HOLDER_EVENT_CODE_ACCOUNT_TEXT_CHANGE = 2;
    public static final int HOLDER_EVENT_CODE_PASSWORD_TEXT_CHANGE = 3;

    private final String[] DEFAULT_EMAIL_SUFFIX_LIST = {"@sina.com", "@163.com", "@qq.com", "@126.com",
            "@vip.sina.com", "@hotmail.com", "@gmail.com", "@sohu.com"};

    private MineApi mMineApi;
    private String mUserAccount;
    private String mPassword;
    private String mCodeId;
    private CapchaDlg mCapchaDlg;
    private boolean mAgreeChecked;  // 是否选中底部同意条款

    @Override
    public ContentHolder onBindContentHolder() {
        return new LoginAccountHolder(mContext);
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
        setPageLabel("login");
        mBaseStatisticHelper.setLastPageRefer(((BaseFrameUIActivity) getActivity()).getRefer());
        // LoginActivity埋点
        ((BaseFrameUIActivity) getActivity()).setPageLabel("login");
        ((BaseFrameUIActivity) getActivity()).setSubmit(true);

        agreeEventObserve();

        requestData();
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

    @Override
    public void onHolderEvent(int eventCode, Bundle bundle) {
        super.onHolderEvent(eventCode, bundle);

        switch (eventCode) {
            case HOLDER_EVENT_CODE_LOGIN:
                // 登录

                //埋点
                mBaseStatisticHelper.assemble1(
                        "btnlogin", null,
                        "click", null,
                        "", null, null).submit();

                mUserAccount = bundle.getString("name");
                mPassword = bundle.getString("password");
                if(mAgreeChecked) {
                    login("", "", true);
                } else {
                    MToastUtils.showShortToast(getString(R.string.checkbox_hint_toast));
                }
                break;
            case HOLDER_EVENT_CODE_ACCOUNT_TEXT_CHANGE:
                // 账号文本框发生变化
                // 埋点
                mBaseStatisticHelper.assemble1(
                        "enterNumber", null,
                        "enter", null,
                        "", null, null).submit();
                break;
            case HOLDER_EVENT_CODE_PASSWORD_TEXT_CHANGE:
                // 账号文本框发生变化
                // 埋点
                mBaseStatisticHelper.assemble1(
                        "enterPassword", null,
                        "enter", null,
                        "", null, null).submit();
                break;
            default:
                break;
        }
    }

    /**
     * 监听底部条款选中状态
     */
    private void agreeEventObserve() {
        LiveEventBus.get(LOGIN_AGREE_STATE, LoginAgreeState.class).observe(this, state -> {
            mAgreeChecked = state.getChecked();
        });
    }

    // 请求数据
    private void requestData() {
        // 有默认值，不显示loading，防止打扰用户正常登录流程

        // 获取邮箱后缀列表
        mMineApi.getEmailExtensions(new NetworkManager.NetworkListener<EmailSuffix>() {
            @Override
            public void onSuccess(EmailSuffix result, String showMsg) {
                if(result == null || CollectionUtils.isEmpty(result.getMailExts())) {
                    setData(Arrays.asList(DEFAULT_EMAIL_SUFFIX_LIST));
                } else {
                    setData(result.getMailExts());
                }
            }

            @Override
            public void onFailure(NetworkException<EmailSuffix> exception, String showMsg) {
                setData(Arrays.asList(DEFAULT_EMAIL_SUFFIX_LIST));
            }
        });
    }

    // 登录
    private void login(String code, String codeId,  boolean first) {
        setPageState(BaseState.LOADING);

        mMineApi.userLogin(mUserAccount, mPassword, code, codeId, "", "", "", "",
                new NetworkManager.NetworkListener<LoginBean>() {
            @Override
            public void onSuccess(LoginBean bean, String showMsg) {
                setPageState(BaseState.SUCCESS);

                if (mCapchaDlg != null) {
                    mCapchaDlg.dismiss();
                }

                if (5 == bean.getStatus()) {
                    // 强制跳转到手机绑定页面,携带者第三方的信息过去
                    JumpUtil.startBindPhoneWithLoginActivity(mContext, assemble().toString(),
                            Constants.BIND_PHONE_LOGIN, mUserAccount, Des.encode(mPassword));
                    return;
                } else if (1 == bean.getStatus()) {
                    // 登录成功
                    MToastUtils.showShortToast(bean.getMsg());
                    JLoginManager.Companion.getInstance().dismissLoginAuthActivity(null, null);
                    // 理论上这里应该是必须有密码的
//                    AccountManager.updateAccountInfo(bean.getUser(), bean.isHasPassword());
                    UserManager.Companion.getInstance().update(UserUtil.toItemUser(bean.getUser()), bean.isHasPassword());
                    // 发送事件
                    EventManager.getInstance().sendLoginEvent(null);
                    // 是否需要绑定手机号
                    if (bean.isNeedBindMobile()) {
                        // 手机绑定页: 2019新版不容许跳过手机绑定
                        JumpUtil.startBindPhoneWithLoginActivity(mContext, assemble().toString(),
                                Constants.BIND_PHONE_LOGIN, mUserAccount, Des.encode(mPassword));
                    }
                    return;
                }
                //登录失败，则清理cookie.
//                NetworkManager.getInstance().mCookieJarManager.clear();
                CookieManager.Companion.getInstance().clear();
                if (bean.getStatus() == 4) {
                    // 状态值4: 请输入图片验证码
                    if (!first) {
                        MToastUtils.showShortToast(bean.getMsg());
                    }
                    requestCapcha(bean.getCodeId(), bean.getCodeUrl());
                } else {
                    MToastUtils.showShortToast(bean.getMsg());
                }
            }

            @Override
            public void onFailure(NetworkException<LoginBean> exception, String showMsg) {
                setPageState(BaseState.SUCCESS);
                if (mCapchaDlg != null) {
                    mCapchaDlg.dismiss();
                }
                MToastUtils.showShortToast(showMsg);
            }
        });
    }

    /**
     * 请求图片验证码
     *
     * @param codeId
     * @param codeUrl
     */
    private void requestCapcha(String codeId, String codeUrl) {
        mCodeId = codeId;
        mCapchaDlg = new CapchaDlg(mContext, CapchaDlg.TYPE_OK_CANCEL, R.layout.dialog_capcha_login);
        mCapchaDlg.setBackgroundDimAmount(0.12f);
        mCapchaDlg.setCanceledOnTouchOutside(false);

        mCapchaDlg.setBtnOKListener(new View.OnClickListener() {

            @Override
            public void onClick(final View v) {
                login(mCapchaDlg.getEditView().getText().toString(), mCodeId, false);

            }
        });

        mCapchaDlg.setBtnCancelListener(new View.OnClickListener() {

            @Override
            public void onClick(final View v) {
                mCapchaDlg.dismiss();
            }
        });

        mCapchaDlg.setCapchaTextListener(new View.OnClickListener() {

            @Override
            public void onClick(final View v) {
                setPageState(BaseState.LOADING);
                mMineApi.getImageVerifyCode(new NetworkManager.NetworkListener<CapchaBean>() {
                    @Override
                    public void onSuccess(CapchaBean result, String showMsg) {
                        setPageState(BaseState.SUCCESS);

                        mCodeId = result.getCodeId();
                        ImageHelper.with(LoginAccountFragment.this)
                                .override(MScreenUtils.dp2px(75), MScreenUtils.dp2px(30))
                                .view(mCapchaDlg.getImageView())
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

        mCapchaDlg.show();
        ImageHelper.with(this)
                .override(MScreenUtils.dp2px(75), MScreenUtils.dp2px(30))
                .view(mCapchaDlg.getImageView())
                .load(codeUrl)
                .asGif()
                .showload();
    }

    public static LoginAccountFragment newInstance() {
        LoginAccountFragment fragment = new LoginAccountFragment();
        fragment.openSubmit();
        return fragment;
    }

    // 页面埋点
    public void openSubmit() {
//        mBaseStatisticHelper.setPageLabel(StatisticTopic.PN_TOPIC_HOME);
//        mBaseStatisticHelper.setSubmit(true);
    }
}
