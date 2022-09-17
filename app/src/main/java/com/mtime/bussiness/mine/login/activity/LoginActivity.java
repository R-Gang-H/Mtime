package com.mtime.bussiness.mine.login.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.alibaba.android.arouter.facade.annotation.Route;

import com.jeremyliao.liveeventbus.LiveEventBus;
import com.kk.taurus.uiframe.d.BaseState;
import com.kk.taurus.uiframe.i.ITitleBar;
import com.kk.taurus.uiframe.v.BaseErrorHolder;
import com.kk.taurus.uiframe.v.BaseStateContainer;
import com.kk.taurus.uiframe.v.ContentHolder;
import com.kk.taurus.uiframe.v.NoTitleBarContainer;
import com.kotlin.android.data.auth.AuthLogin;
import com.kotlin.android.router.RouterManager;
import com.kotlin.android.app.router.liveevent.event.LoginState;
import com.kotlin.android.share.auth.AuthManager;
import com.kotlin.android.share.auth.AuthPlatform;
import com.kotlin.android.share.auth.AuthState;
import com.kotlin.android.user.UserManager;
import com.kotlin.android.app.data.entity.user.User;
import com.kotlin.android.user.login.UserLoginKt;
import com.kotlin.android.user.login.jguang.JLoginManager;
import com.mtime.R;
import com.mtime.account.UserUtil;
import com.mtime.base.network.NetworkException;
import com.mtime.base.network.NetworkManager;
import com.mtime.base.utils.AppUtils;
import com.mtime.base.utils.MToastUtils;
import com.mtime.bussiness.mine.api.MineApi;
import com.mtime.bussiness.mine.login.bean.ThirdLoginBean;
import com.mtime.bussiness.mine.login.holder.LoginHolder;
import com.mtime.constant.Constants;
import com.mtime.event.EventManager;
import com.mtime.frame.App;
import com.mtime.frame.BaseFrameUIActivity;
import com.mtime.util.JumpUtil;
import com.mtime.util.MtimeUtils;
import com.mtime.util.ToolsUtils;

import kotlin.Unit;

import static com.kotlin.android.app.router.path.RouterActivityPath.AppUser.PAGER_LOGIN;
import static com.kotlin.android.router.RouterModelKt.KEY_LOGIN_NAVIGATION_PATH;
import static com.kotlin.android.router.liveevent.EventKeyExtKt.LOGIN_STATE;

/**
 * @author vivian.wei
 * @date 2019/7/31
 * @desc 登录页：短信验证码登录|账号密码登录
 */
@Route(path = PAGER_LOGIN)
public class LoginActivity extends BaseFrameUIActivity<Void, LoginHolder> {

    private static final String PLATFORMID_WEIBO = "1";
    private static final String PLATFORMID_QQ = "2";
    private static final String PLATFORMID_WEIXIN = "4";
    public static final int HOLDER_EVENT_CODE_WECHAT = 101;
    public static final int HOLDER_EVENT_CODE_WEIBO = 102;
    public static final int HOLDER_EVENT_CODE_QQ = 103;
    public static final int HOLDER_EVENT_CODE_ACCOUNT_LOGIN_LINK_CLICK = -1000;
    public static final int HOLDER_EVENT_CODE_SMS_CODE_LOGIN_LINK_CLICK = -1002;
    public static final int HOLDER_EVENT_FORGET_PASSWORD_LINK_CLICK = -1003;

    private int mRequestCode = -1;
    private String mTargetActivityId;
    private MineApi mMineApi;

    @Override
    protected BaseStateContainer getStateContainer() {
        return new NoTitleBarContainer(this, this, this);
    }

    @Override
    public ContentHolder onBindContentHolder() {
        return new LoginHolder(this, getSupportFragmentManager());
    }

    @Override
    public BaseErrorHolder onBindErrorHolder() {
        return super.onBindErrorHolder();
    }

    @Override
    protected void onErrorRetry() {
        super.onErrorRetry();

        onLoadState();
    }

    @Override
    protected void onParseIntent() {
        super.onParseIntent();

        mRequestCode = getIntent().getIntExtra(App.getInstance().KEY_REQUEST_CODE, -1);
        mTargetActivityId = getIntent().getStringExtra(App.getInstance().KEY_TARGET_ACTIVITY_ID);

    }

    @Override
    protected void onInit(Bundle savedInstanceState) {
        super.onInit(savedInstanceState);

        if (null == mMineApi) {
            mMineApi = new MineApi();
        }

        AuthManager.INSTANCE.install(this, (authData) -> {
            AuthState state = authData.getState();
            switch (state) {
                case SUCCESS:
//                    ToastExtKt.showToast("三方授权成功");
                    thirdAuthSuccess(authData.getAuthLogin());
                    break;
                case CANCEL:
//                    ToastExtKt.showToast("取消三方登录");
                    break;
                case FAILURE:
//                    ToastExtKt.showToast("三方登录失败");
                    break;
            }

            return Unit.INSTANCE;
        });

        loginEventObserve();
    }

    private void thirdAuthSuccess(AuthLogin authLogin) {
        String token = null == authLogin.getAccessToken() ? "" : authLogin.getAccessToken();
        String expires = null == authLogin.getQqExpiresIn() ? "" : authLogin.getQqExpiresIn();
        int platformId = null == authLogin.getPlatformId() ? 0 : authLogin.getPlatformId();
        String code = null == authLogin.getCode() ? "" : authLogin.getCode();

        userOauthLogin(token, expires, String.valueOf(platformId), code);
    }

    @Override
    protected void onLoadState() {
        setPageState(BaseState.SUCCESS);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        AuthManager.INSTANCE.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onHolderEvent(int eventCode, Bundle bundle) {
        super.onHolderEvent(eventCode, bundle);

        switch (eventCode) {
            case HOLDER_EVENT_CODE_WECHAT:
                // 微信登录
                // 埋点
                mBaseStatisticHelper.assemble1(
                        "thirdpartyLogin", null,
                        "weChat", null,
                        "click", null, null).submit();

                if (!AppUtils.isWeChatInstalled(this)) {
                    MToastUtils.showLongToast(getResources().getString(R.string.login_wx_install_tip));
                    return;
                }
                AuthManager.INSTANCE.auth(AuthPlatform.WE_CHAT);
//                ThirdLoginManager.getInstance(getApplicationContext()).wechatLogin(
//                        App.WX_APP_ID,
//                        new ThirdLoginListener() {
//                            @Override
//                            public void onLoginSuccess(LoginResultBean resultBean) {
//                                userOauthLogin("", "", PLATFORMID_WEIXIN, resultBean.wechatCode);
//                            }
//
//                            @Override
//                            public void onError(String s) {
//                            }
//                        });
                break;
            case HOLDER_EVENT_CODE_WEIBO:
                // 微博登录
                //埋点
                mBaseStatisticHelper.assemble1(
                        "thirdpartyLogin", null,
                        "weibo", null,
                        "click", null, null).submit();

                AuthManager.INSTANCE.auth(AuthPlatform.WEI_BO);
//                ThirdLoginManager.getInstance(this.getApplicationContext()).sinaLogin(
//                        new ThirdLoginListener() {
//                            @Override
//                            public void onLoginSuccess(LoginResultBean resultBean) {
//                                userOauthLogin(resultBean.access_token, resultBean.expires_in, PLATFORMID_WEIBO, "");
//                            }
//
//                            @Override
//                            public void onError(String s) {
//                                MToastUtils.showShortToast(s);
//                            }
//                        });
                break;
            case HOLDER_EVENT_CODE_QQ:
                // QQ登录
                //埋点
                mBaseStatisticHelper.assemble1(
                        "thirdpartyLogin", null,
                        "qq", null,
                        "click", null, null).submit();
                AuthManager.INSTANCE.auth(AuthPlatform.QQ);
//                ThirdLoginManager.getInstance(this.getApplicationContext()).qqLogin(
//                        new ThirdLoginListener() {
//                            @Override
//                            public void onLoginSuccess(LoginResultBean resultBean) {
//                                userOauthLogin(resultBean.access_token, resultBean.expires_in, PLATFORMID_QQ, "");
//                            }
//
//                            @Override
//                            public void onError(String s) {
//                                MToastUtils.showShortToast(s);
//                            }
//                        });
                break;
            case ITitleBar.TITLE_BAR_EVENT_NAVIGATION_CLICK:
                // 埋点：点击"返回"箭头埋点
                mBaseStatisticHelper.assemble1(
                        "back", null,
                        "click", null,
                        "", null, null).submit();
                break;
            case HOLDER_EVENT_CODE_ACCOUNT_LOGIN_LINK_CLICK:
                // 点击"账号密码登录"链接
                // 埋点
                mBaseStatisticHelper.assemble1(
                        "loginSwitch", null,
                        "click", null,
                        "", null, null).submit();
                break;
            case HOLDER_EVENT_CODE_SMS_CODE_LOGIN_LINK_CLICK:
                // 点击"短信验证码登录"链接
                // 埋点
                mBaseStatisticHelper.assemble1(
                        "registerSwitch", null,
                        "click", null,
                        "", null, null).submit();
                break;
            case HOLDER_EVENT_FORGET_PASSWORD_LINK_CLICK:
                // 点击"找回密码"链接
                // 埋点
                mBaseStatisticHelper.assemble1(
                        "RetrievePassword", null,
                        "click", null,
                        "", null, null).submit();
                break;
            default:
                break;
        }
    }

    @Override
    protected boolean openEventBus() {
        return true;
    }

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onLogin(LoginEvent event) {
//        // 用于同步收藏列表
//        ToolsUtils.syncFavorites();
//        ToolsUtils.sendConfigMsg(getApplicationContext());
//        updateUserInfo();
//        LiveEventBus.get(LOGIN_STATE).post(new LoginState(true));//发送登录成功事件
//        Bundle extras = getIntent().getExtras();
//        assert extras != null;
//        String keyPath = extras.getString("key_login_navigation_path");
//        RouterManager.INSTANCE.navigation(keyPath, extras);
//        finish();
//
//    }

    private void loginEventObserve() {
        LiveEventBus.get(LOGIN_STATE, LoginState.class).observe(this, loginState -> {
            if (loginState.isLogin()) {//登录成功
                // 用于同步收藏列表
                ToolsUtils.syncFavorites();
                ToolsUtils.sendConfigMsg(getApplicationContext());
                updateUserInfo(loginState.getBundle());
            }
        });
    }

    /**
     * 时光账号登录成功后，请求用户信息接口，并更新用户信息
     */
    private void updateUserInfo(Bundle bundle) {
        setPageState(BaseState.LOADING);

        mMineApi.getAccountDetail(new NetworkManager.NetworkListener<User>() {
            @Override
            public void onSuccess(User result, String showMsg) {
                setPageState(BaseState.SUCCESS);
                if(result == null) {
                    return;
                }

//                AccountManager.updateAccountInfo(result);
                UserManager.Companion.getInstance().update(result);

                tryJumpToNextPage(bundle);

                if (-1 != mRequestCode) {
                    setResult(App.STATUS_LOGIN);
                }
                finish();
            }

            @Override
            public void onFailure(NetworkException<User> exception, String showMsg) {
                setPageState(BaseState.SUCCESS);
                MToastUtils.showShortToast("更新用户信息失败:" + showMsg);
                tryJumpToNextPage(bundle);
                finish();
            }
        });
    }

    private void tryJumpToNextPage(@Nullable Bundle bundle) {
        if (!TextUtils.isEmpty(mTargetActivityId)) {
            MtimeUtils.startActivityWithID(getApplicationContext(), mTargetActivityId);
        } else if (bundle != null) {
            String keyPath = bundle.getString(KEY_LOGIN_NAVIGATION_PATH);
            if (!TextUtils.isEmpty(keyPath)) {
                RouterManager.Companion.getInstance().navigation(
                        keyPath,
                        bundle,
                        null,
                        -1,
                        0,
                        false,
                        null
                );
            }
        }
    }

    // 第三方授权登录
    private void userOauthLogin(String token, String expires, String platformId, String code) {
        setPageState(BaseState.LOADING);

        mMineApi.userOauthLogin(token, expires, platformId, code, "", "", "",
                new NetworkManager.NetworkListener<ThirdLoginBean>() {
                    @Override
                    public void onSuccess(ThirdLoginBean bean, String showMsg) {
                        setPageState(BaseState.SUCCESS);

                        if (4 == bean.getStatus() || 2 == bean.getStatus()) {
                            // 2:没有注册 4:强制绑定
                            // 强制绑定手机号。 跳转到绑定手机绑定页面,携带者第三方的信息过去
                            JumpUtil.startBindPhoneWithLoginActivity(LoginActivity.this, assemble().toString(),
                                    Constants.BIND_PHONE_THIRD_LOGIN, bean.getToken(), token, code, platformId, expires, mTargetActivityId,
                                    bean.isHasPassword(), 2 != bean.getStatus(), mRequestCode);
                            finish();
                            return;
                        } else if (1 == bean.getStatus()) {
                            // 1:登录成功
//                            AccountManager.updateAccountInfo(bean.getUser(), bean.isHasPassword());
                            UserManager.Companion.getInstance().update(UserUtil.toItemUser(bean.getUser()), bean.isHasPassword());
                            JLoginManager.Companion.getInstance().dismissLoginAuthActivity(null, null);
                            // 发送事件
                            EventManager.getInstance().sendLoginEvent(mTargetActivityId);
                            // 是否需要绑定手机号
                            if (bean.isNeedBindMobile()) {
                                // 可以跳过的绑定（2019新版必须绑定手机号）
                                JumpUtil.startBindPhoneWithLoginActivity(LoginActivity.this, assemble().toString(),
                                        Constants.BIND_PHONE_THIRD_LOGIN, bean.getToken(), token, code, platformId, expires, mTargetActivityId,
                                        bean.isHasPassword(), bean.getStatus() != 2, mRequestCode);
                            } else {
                                MtimeUtils.startActivityWithID(getApplicationContext(), mTargetActivityId);
                            }

                            if (-1 != mRequestCode) {
                                setResult(App.STATUS_LOGIN);
                            }
                            finish();
                        } else {
                            MToastUtils.showShortToast("登录时光网失败:" + bean.getMsg());
                        }
                    }

                    @Override
                    public void onFailure(NetworkException<ThirdLoginBean> exception, String showMsg) {
                        setPageState(BaseState.SUCCESS);
                        MToastUtils.showShortToast("登录时光网失败:" + showMsg);
                    }
                });
    }

    /**
     * 自己定义refer
     *
     * @param context
     * @param refer
     */
    public static void launch(Context context, String refer) {
        UserLoginKt.gotoLoginPage(context, null, null);

        // ####################

//        Intent launcher = new Intent(context, LoginActivity.class);
//        dealRefer(context, refer, launcher);
//        context.startActivity(launcher);
    }

    public static void launch(Context context, String refer, int requestCode) {
        Bundle bundle = new Bundle();
        bundle.putInt(App.getInstance().KEY_REQUEST_CODE, requestCode);

        UserLoginKt.gotoLoginPage(context, bundle, requestCode);

        // ####################

//        Intent intent = new Intent(context, LoginActivity.class);
//        intent.putExtra(App.getInstance().KEY_REQUEST_CODE, requestCode);
//        dealRefer(context, refer, intent);
//        if (context instanceof Activity) {
//            ((Activity) context).startActivityForResult(intent, requestCode);
//        } else {
//            context.startActivity(intent);
//        }
    }

    // 选座页，未登录情况下，点击下一步（进入订单确认页），会调用该方法
    public static void launch(Context context, String refer, double mTotalPrice, String mIntroduction, String movieName, String cinemaName,
                              String mSeatId, int selectedSeatCount, double serviceFee, String subOrderID, String mTicketDateInfo, String mSeatSelectedInfo, String mDId,
                              String mMovieId, String mCinemaId, String mDate, boolean show_not_vip, boolean show_new_gif_dlg, int requestCode) {
        Bundle bundle = new Bundle();
        bundle.putDouble(App.getInstance().KEY_SEATING_TOTAL_PRICE, mTotalPrice);
        bundle.putString(App.getInstance().KEY_SEATING_PRICE_INTRODUCTION, mIntroduction);
        bundle.putString(App.getInstance().KEY_MOVIE_NAME, movieName);
        bundle.putString(App.getInstance().KEY_CINEMA_NAME, cinemaName);
        bundle.putString(App.getInstance().KEY_SEATING_SEAT_ID, mSeatId);
        bundle.putInt(App.getInstance().KEY_SEATING_SELECTED_SEAT_COUNT, selectedSeatCount); // 座位数
        bundle.putDouble(App.getInstance().KEY_SEATING_SERVICE_FEE, serviceFee);
        bundle.putString(App.getInstance().KEY_SEATING_SUBORDER_ID, subOrderID);
        bundle.putString(App.getInstance().KEY_TICKET_DATE_INFO, mTicketDateInfo);
        bundle.putString(App.getInstance().KEY_SEAT_SELECTED_INFO, mSeatSelectedInfo);
        // 以下4个数据需要带到支付页，当需要从支付页重新返回选座页时要带回来（更换场次要用到）
        bundle.putString(App.getInstance().KEY_SEATING_DID, mDId);
        bundle.putString(App.getInstance().KEY_MOVIE_ID, mMovieId);
        bundle.putString(App.getInstance().KEY_CINEMA_ID, mCinemaId);
        bundle.putString(App.getInstance().KEY_SHOWTIME_DATE, mDate);

        bundle.putBoolean(App.getInstance().KEY_SHOW_NOT_VIP, show_not_vip);
        bundle.putInt(App.getInstance().KEY_REQUEST_CODE, requestCode);
        bundle.putBoolean(App.getInstance().KEY_SHOW_NEWGIFT_DLG, show_new_gif_dlg);

        UserLoginKt.gotoLoginPage(context, bundle, requestCode);

        // ####################

//        final Intent intent = new Intent(context, LoginActivity.class);
//        intent.putExtra(App.getInstance().KEY_SEATING_TOTAL_PRICE, mTotalPrice);
//        intent.putExtra(App.getInstance().KEY_SEATING_PRICE_INTRODUCTION, mIntroduction);
//        intent.putExtra(App.getInstance().KEY_MOVIE_NAME, movieName);
//        intent.putExtra(App.getInstance().KEY_CINEMA_NAME, cinemaName);
//        intent.putExtra(App.getInstance().KEY_SEATING_SEAT_ID, mSeatId);
//        intent.putExtra(App.getInstance().KEY_SEATING_SELECTED_SEAT_COUNT, selectedSeatCount); // 座位数
//        intent.putExtra(App.getInstance().KEY_SEATING_SERVICE_FEE, serviceFee);
//        intent.putExtra(App.getInstance().KEY_SEATING_SUBORDER_ID, subOrderID);
//        intent.putExtra(App.getInstance().KEY_TICKET_DATE_INFO, mTicketDateInfo);
//        intent.putExtra(App.getInstance().KEY_SEAT_SELECTED_INFO, mSeatSelectedInfo);
//        // 以下4个数据需要带到支付页，当需要从支付页重新返回选座页时要带回来（更换场次要用到）
//        intent.putExtra(App.getInstance().KEY_SEATING_DID, mDId);
//        intent.putExtra(App.getInstance().KEY_MOVIE_ID, mMovieId);
//        intent.putExtra(App.getInstance().KEY_CINEMA_ID, mCinemaId);
//        intent.putExtra(App.getInstance().KEY_SHOWTIME_DATE, mDate);
//
//        intent.putExtra(App.getInstance().KEY_SHOW_NOT_VIP, show_not_vip);
//        intent.putExtra(App.getInstance().KEY_REQUEST_CODE, requestCode);
//        intent.putExtra(App.getInstance().KEY_SHOW_NEWGIFT_DLG, show_new_gif_dlg);
//        dealRefer(context, refer, intent);

//        if (context instanceof Activity) {
//            ((Activity) context).startActivityForResult(intent, requestCode);
//        } else {
//            context.startActivity(intent);
//        }
    }

}
