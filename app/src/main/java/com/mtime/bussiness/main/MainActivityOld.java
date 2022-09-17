//package com.mtime.bussiness.main;
//
//import static com.kotlin.android.router.liveevent.EventKeyExtKt.CLOSE_BONUS_SCENE;
//import static com.kotlin.android.router.liveevent.EventKeyExtKt.LOGIN_STATE;
//import static com.kotlin.android.router.liveevent.EventKeyExtKt.POPUP_BONUS_SCENE;
//
//import android.app.Activity;
//import android.content.Context;
//import android.content.Intent;
//import android.net.Uri;
//import android.os.Bundle;
//import android.text.TextUtils;
//
//import androidx.fragment.app.Fragment;
//import androidx.fragment.app.FragmentActivity;
//import androidx.lifecycle.Observer;
//
//import com.google.android.material.tabs.TabLayout;
//import com.jeremyliao.liveeventbus.LiveEventBus;
//import com.kk.taurus.uiframe.v.BaseStateContainer;
//import com.kk.taurus.uiframe.v.NoTitleBarContainer;
//import com.kotlin.android.app.data.entity.bonus.PopupBonusScene;
//import com.kotlin.android.app.router.liveevent.event.LoginState;
//import com.kotlin.android.audio.floatview.component.aduiofloat.FloatingView;
//import com.kotlin.android.bonus.scene.component.BonusSceneExtKt;
//import com.kotlin.android.bonus.scene.component.bean.BonusSceneDialogDismissBean;
//import com.kotlin.android.bonus.scene.component.bean.PopupBonusSceneBean;
//import com.kotlin.android.core.statusbar.StatusBarUtils;
//import com.kotlin.android.ktx.ext.statusbar.StatusBarExtKt;
//import com.kotlin.android.user.UserManager;
//import com.kotlin.chat_component.HuanxinConnector;
//import com.mtime.R;
//import com.mtime.applink.ApplinkManager;
//import com.mtime.base.network.NetworkException;
//import com.mtime.base.network.NetworkManager;
//import com.mtime.base.statistic.app.StatisticApp;
//import com.mtime.base.statistic.bean.StatisticPageBean;
//import com.mtime.base.utils.MToastUtils;
//import com.mtime.bussiness.main.api.MainApi;
//import com.mtime.bussiness.main.maindialog.MainDialogManager;
//import com.mtime.constant.Constants;
//import com.mtime.frame.App;
//import com.mtime.frame.BaseActivity;
//import com.mtime.statistic.large.StatisticManager;
//import com.mtime.statistic.large.home.StatisticHome;
//import com.mtime.util.SystemUtil;
//
//import java.util.HashMap;
//
///**
// * @author ZhouSuQiang
// * @date 2018/8/29
// */
//public class MainActivityOld extends BaseActivity<String, MainHolder> {
//    public static final String KEY_MAIN_TAB_INDEX = Constants.KEY_MAIN_TAB_INDEX;
//    private static final String KEY_SPLASH_JUMP_URL = "key_splash_jump_url";
//    private MainApi mainApi;
//
//    /**
//     * @param context                        上下文
//     * @param tabIndex                       主页面导航索引
//     * @param applink                        applink数据
//     * @param ticketType                     票务类型，具体值可参考TabTicketFragment
//     * @param isForceRefreshShowcinema       是否强制刷新影院列表
//     * @param videoChannelSelectedSubTabType 选择视频频道下子tab的类型，具体值参考VideoHomeNavHelper
//     * @param refer                          页面路劲，上报使用
//     */
//    public static void launch(Context context, int tabIndex, String applink,
//                              int ticketType, boolean isForceRefreshShowcinema,
//                              int videoChannelSelectedSubTabType,
//                              String refer) {
//        Intent intent = new Intent();
//        intent.putExtra(KEY_MAIN_TAB_INDEX, tabIndex);
//        if (!TextUtils.isEmpty(applink)) {
//            intent.putExtra(KEY_SPLASH_JUMP_URL, applink);
//        }
//        if (ticketType > 0) {
//            intent.putExtra(App.getInstance().MAIN_TAB_BUYTICKET_TYPE, ticketType);
//        }
//        intent.putExtra(App.getInstance().MAIN_TAB_BUYTICKET_CINEMA_LIST_REFRESH, isForceRefreshShowcinema);
////        if (videoChannelSelectedSubTabType > -1) {
////            intent.putExtra(VideoHomeFragment.KEY_SUB_PAGE_SELECTED_TAB_TYPE, videoChannelSelectedSubTabType);
////        }
//        intent.setClass(context, MainActivityOld.class);
//        dealRefer(context, refer, intent);
//        context.startActivity(intent);
//    }
//
//
//    private MainDialogManager mMainDialogManager;
//    private BottomNavigationHelper mBottomNavigationHelper;
//    private boolean mExitApp;
//
//    @Override
//    public void onBackPressed() {
//        Fragment fragment = mBottomNavigationHelper.getCurFragment();
//        if (fragment instanceof MainCommunicational) {
//            if (((MainCommunicational) fragment).onHandleMainEvent(MainCommunicational.EVENT_ON_BACK_PRESSED, null)) {
//                return;
//            }
//        }
//
//        if (mExitApp) {
//            FloatingView.get().remove();
//            super.onBackPressed();
//        } else {
//            MToastUtils.showShortToast(R.string.exit_app_hint);
//            mExitApp = true;
//            postDelayed(() -> mExitApp = false, 2000);
//        }
//    }
//
//    @Override
//    protected void onInit(Bundle savedInstanceState) {
//        super.onInit(savedInstanceState);
//        setSubmit(false);
//        setPageLabel(StatisticHome.PN_HOME);
//
//
//        //设置状态栏透明
////        StatusBarUtil.setTranslucentStatus(this,true);
////        if (!StatusBarUtil.setStatusBarDarkTheme(this, true)) {
////            //如果不支持设置深色风格 为了兼容总不能让状态栏白白的看不清, 于是设置一个状态栏颜色为半透明,
////            //这样半透明+白=灰, 状态栏的文字能看得清
////            StatusBarUtil.setStatusBarColor(this,0x55000000);
////        }
////        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
////        ActivityExtKt.setStatusBarColor(this, ContextCompat.getColor(this,R.color.translate));
////        设置全屏
//        StatusBarExtKt.handleArticleStatusBar(this, false);
////        修改字体颜色
//        if (StatusBarUtils.INSTANCE.canControlStatusBarTextColor()) {
//            StatusBarUtils.INSTANCE.translucentStatusBar(this, true, true);
//        }
//
//        mMainDialogManager = new MainDialogManager();
//        mMainDialogManager.init(this);
//        mBottomNavigationHelper = new BottomNavigationHelper();
//        mBottomNavigationHelper.setup(getUserContentHolder().mTabLayout, getSupportFragmentManager());
//        getUserContentHolder().mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
//            @Override
//            public void onTabSelected(TabLayout.Tab tab) {
//                switch (tab.getPosition()) {
//                    case 0:
//                        // 主页面
//                        //app运行中显示业务弹框
//                        mMainDialogManager.appRuningShow();
//                        break;
//
//                    default:
//                        break;
//                }
//            }
//
//            @Override
//            public void onTabUnselected(TabLayout.Tab tab) {
//            }
//
//            @Override
//            public void onTabReselected(TabLayout.Tab tab) {
//            }
//        });
//
//        processIntent(getIntent());
//        submitPushState();
//        popupEvent();
//        //连接环信
//        HuanxinConnector.Companion.getInstance().connect();
//        huanxinEvent();
//    }
//
//    private void huanxinEvent() {
//        LiveEventBus.get(LOGIN_STATE, LoginState.class).observe(MainActivityOld.this, new Observer<LoginState>() {
//            @Override
//            public void onChanged(LoginState loginState) {
//                if (loginState.isLogin()){
//                    HuanxinConnector.Companion.getInstance().connect();
//                }else {
//                    HuanxinConnector.Companion.getInstance().disConnect();
//                }
//            }
//        });
//    }
//
//    private void popupEvent() {
//        LiveEventBus.get(POPUP_BONUS_SCENE).observe(MainActivityOld.this, new Observer<Object>() {
//            @Override
//            public void onChanged(Object popupBonusSceneBean) {
//                if (!UserManager.Companion.getInstance().isLogin()) return;
//                postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (popupBonusSceneBean != null && popupBonusSceneBean instanceof PopupBonusSceneBean) {
//                            checkPopupBonus(((PopupBonusSceneBean) popupBonusSceneBean).getAction());
//                        }
//                    }
//                }, 1000L);
//
//
//            }
//        });
//    }
//
//    private void checkPopupBonus(long action) {
//        if (mainApi != null) {
//            mainApi.checkPopupBonus(action, new NetworkManager.NetworkListener<PopupBonusScene>() {
//                @Override
//                public void onSuccess(PopupBonusScene result, String showMsg) {
//                    if (result != null) {
//                        if (result.getCode() == 0L && result.getSuccess()) {
//                            Activity topActivity = App.getInstance().getTopActivity();
//                            if (topActivity != null && topActivity instanceof FragmentActivity && !topActivity.isFinishing() && !topActivity.isDestroyed()) {
//                                BonusSceneExtKt.showBonusSceneDialog(((FragmentActivity) topActivity), action);
//                            } else {
//                                postCloseBonusSceneDialog();
//                            }
//                        } else {
//                            postCloseBonusSceneDialog();
//                        }
//                    } else {
//                        postCloseBonusSceneDialog();
//                    }
//                }
//
//                @Override
//                public void onFailure(NetworkException<PopupBonusScene> exception, String showMsg) {
//                    postCloseBonusSceneDialog();
//                }
//            });
//        }
//    }
//
//    private void postCloseBonusSceneDialog() {
//        LiveEventBus.get(CLOSE_BONUS_SCENE).post(new BonusSceneDialogDismissBean(true));
//    }
//
//    @Override
//    protected void onNewIntent(Intent intent) {
//        super.onNewIntent(intent);
//        processIntent(intent);
//    }
//
//    @Override
//    public MainHolder onBindContentHolder() {
//        return new MainHolder(this);
//    }
//
//    @Override
//    protected BaseStateContainer getStateContainer() {
//        return new NoTitleBarContainer(this, this, this);
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//
//        StatisticPageBean bean = assemble(StatisticApp.APP_CLOSE, null, null, null, null, null, null);
//        bean.pageName = StatisticApp.PAGE_APP;
//        StatisticManager.getInstance().submit(bean);
//
//        if (null != mMainDialogManager) {
//            mMainDialogManager.destroy();
//            mMainDialogManager = null;
//        }
//        if (null != mBottomNavigationHelper) {
//            mBottomNavigationHelper.destroy();
//        }
//    }
//
//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        // TODO: 2020/12/25 为了解决fragment重叠问题，临时采用屏蔽系统保存相关信息，
//        //  待后期MainActivity重构时可采用保存fragment再读取fragment方案，
//        //  可参考https://blog.csdn.net/yuzhiqiang_1993/article/details/75014591
////        super.onSaveInstanceState(outState);
//    }
//
//    /**
//     * 上报推送状态
//     */
//    private void submitPushState() {
//        //推送状态监测
//        HashMap<String, String> params = new HashMap<>(1);
//        params.put(StatisticApp.APP_PUSH_STATE_TYPE, SystemUtil.checkNotificationPermission(this) ? "open" : "close");
//        StatisticPageBean bean = assemble(StatisticApp.APP_PUSH_STATE, null, null, null, null, null, params);
//        bean.pageName = StatisticApp.PAGE_APP;
//        StatisticManager.getInstance().submit(bean);
//    }
//
//    /**
//     * 处理intent
//     */
//    private void processIntent(Intent intent) {
//        mainApi = new MainApi();
//        if (null == intent) {
//            return;
//        }
//
//        // 外部h5调用起来
//        if (TextUtils.equals("mtime", intent.getScheme())) {
//            String deeplink = intent.getDataString();
//            if (!TextUtils.isEmpty(deeplink)) {
//                Uri data = intent.getData();
//                if (data != null) {
//                    String applinkData = data.getQueryParameter("applinkData");
//                    if (!TextUtils.isEmpty(applinkData)) {
//                        ApplinkManager.jump4Scheme(this, applinkData);
//                    }
//                }
//            }
//        } else {
//            String url = intent.getStringExtra(KEY_SPLASH_JUMP_URL);
//            if (!TextUtils.isEmpty(url)) {
//                ApplinkManager.jump(this, url, assemble().toString());
//            }
//
//            int tabIndex = intent.getIntExtra(KEY_MAIN_TAB_INDEX, 0);
//            if (tabIndex >= 0) {
//                getUserContentHolder().selectedTab(tabIndex);
//            }
//            postDelayed(() -> {
//                Fragment fragment = mBottomNavigationHelper.getCurFragment();
//                if (fragment instanceof MainCommunicational) {
//                    ((MainCommunicational) fragment).onHandleMainEvent(MainCommunicational.EVENT_ON_PARSE_INTENT, intent.getExtras());
//                }
//            }, 500);
//        }
//    }
//}
