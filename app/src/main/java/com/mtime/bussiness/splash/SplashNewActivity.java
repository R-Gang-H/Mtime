package com.mtime.bussiness.splash;

import static com.kotlin.android.core.ext.CoreAppExtKt.getSpValue;
import static com.kotlin.android.core.ext.CoreAppExtKt.getVersionCode;
import static com.kotlin.android.core.ext.CoreAppExtKt.putSpValue;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;

import com.kk.taurus.uiframe.v.BaseStateContainer;
import com.kk.taurus.uiframe.v.ContentHolder;
import com.kk.taurus.uiframe.v.NoTitleBarContainer;
import com.kotlin.chat_component.HuanxinLongConnectionResolver;
import com.kotlin.android.retrofit.cookie.CookieManager;
import com.kotlin.android.router.ext.ProviderExtKt;

import com.kotlin.android.app.router.provider.splash.ISplashProvider;
import com.kotlin.android.user.PrivacyKt;
import com.kotlin.android.widget.dialog.BaseDialog;
import com.mtime.base.network.NetworkException;
import com.mtime.base.network.NetworkManager;
import com.mtime.base.statistic.app.StatisticApp;
import com.mtime.bussiness.common.bean.CommonAdListBean;
import com.mtime.bussiness.main.maindialog.bean.PricacyPolicyBean;
import com.mtime.bussiness.main.maindialog.dialog.PrivacyDialog;
import com.mtime.bussiness.main.maindialog.dialog.PrivacyPolicyDialog;
import com.mtime.bussiness.mine.activity.SettingActivity;
import com.mtime.bussiness.splash.api.SplashApi;
import com.mtime.bussiness.splash.bean.SplashStartLoad;
import com.mtime.bussiness.splash.holder.SplashHolder;
import com.mtime.common.cache.CacheManager;
import com.mtime.common.utils.LogWriter;
import com.mtime.constant.FrameConstant;
import com.mtime.frame.App;
import com.mtime.frame.BaseFrameUIActivity;
import com.mtime.widgets.NetworkImageView;

/**
 * Created by CSY on 2018/4/27.
 * <>
 */
public class SplashNewActivity extends BaseFrameUIActivity<CommonAdListBean, SplashHolder> {

    private SplashApi mSplashApi;
    private boolean isNotNormalActivity = false;

    @Override
    public ContentHolder onBindContentHolder() {
        return new SplashHolder(this);
    }

    @Override
    public BaseStateContainer getStateContainer() {
        NoTitleBarContainer container = new NoTitleBarContainer(this, this, this);
        container.getHolderView().setBackgroundColor(Color.TRANSPARENT);
        return container;
    }

    private boolean isNotNormalActivity(){
        return  !isTaskRoot() && Intent.ACTION_MAIN.equals(getIntent().getAction()) && getIntent().hasCategory(Intent.CATEGORY_LAUNCHER);
    }

    @Override
    protected void onInit(Bundle savedInstanceState) {
        super.onInit(savedInstanceState);
        isNotNormalActivity = isNotNormalActivity();
        if (isNotNormalActivity){
            finish();
            return;
        }

        //????????????????????????
        HuanxinLongConnectionResolver.INSTANCE.registerHuanxinMessageListener();
        setPageLabel(StatisticApp.PAGE_APP);

        // ????????????????????????
        if (isShowPrivacyDialog()) {
            showPrivacyDialog();
        } else {
            launch();
        }

//        mSplashApi = new SplashApi();
        //??????????????????
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }

    @Override
    protected void onLoadState() {
        super.onLoadState();
    }

    private void load() {
        mSplashApi = new SplashApi();

        boolean status = initState();
        //????????????????????????
        LoadManager.initLoadInfo();

        mSplashApi.startUpload(status, new NetworkManager.NetworkListener<SplashStartLoad>() {
            @Override
            public void onSuccess(SplashStartLoad splashStartLoad, String s) {
                LogWriter.e("checkTime", "endok time:" + System.currentTimeMillis());
                //????????????
//                splashStartLoad.getEntry().setAndroid(3);
//                splashStartLoad.getLoadingIcon().setLoadIcon("https://img.zcool.cn/community/015c615900a746a801214550086613.gif");
                LoadManager.saveLoadInfo(splashStartLoad);
                LoadManager.parserEntryData(splashStartLoad);
            }

            @Override
            public void onFailure(NetworkException<SplashStartLoad> e, String s) {
                LogWriter.e("checkTime", "ender time:" + System.currentTimeMillis() + e.getMessage());
            }
        });

        // ??????????????????
        ISplashProvider provider = ProviderExtKt.getProvider(ISplashProvider.class);
        if (null != provider) {
            provider.startSplashActivity();
        }
        postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 3000);
    }

    private boolean initState() {
        boolean status = true;
        int vName = App.getInstance().getPrefsManager().getInt(FrameConstant.PACKAGE_VERSION);
        if (vName == 0) {
            int times = App.getInstance().getPrefsManager().getInt("start_times");
            CacheManager.getInstance().cleanAllFileCache();
            App.getInstance().getPrefsManager().putInt(FrameConstant.PACKAGE_VERSION, 1);
            if (0 == times && !CookieManager.Companion.getInstance().isCookieExistByName("_mi_")) {
                status = false;
            }
        }

        int times = App.getInstance().getPrefsManager().getInt("start_times");
        if (times == 0) {
            App.getInstance().getPrefsManager().putBoolean(SettingActivity.KEY_REMIND_NEW_MOVIE, true);
            App.getInstance().getPrefsManager().putBoolean(SettingActivity.KEY_UPDATE_VER, true);
        }

        App.getInstance().getPrefsManager().putInt("start_times", ++times);
        App.getInstance().getPrefsManager().putBoolean("new_activities_show", false);

        // ???????????????
        if (App.getInstance().REGISTER_DLG_NEWGIFT_IMG != null) {
            App.getInstance().REGISTER_DLG_NEWGIFT_IMG = null;
        }

        // ????????????????????????????????????
//        App.getInstance().FILTER_SET = App.getInstance().getPrefsManager().getBoolean(App.getInstance().KEY_FILTER_SET);
//        todo ???????????????????????????????????????????????????????????????????????????????????????????????? 2020/09/15
        App.getInstance().FILTER_SET = false;
        App.getInstance().getPrefsManager().putBoolean(App.getInstance().KEY_FILTER_SET, false);

        NetworkImageView.FILTER_SET_ON = App.getInstance().FILTER_SET;

        return status;
    }

    /**
     * ?????????????????????
     */
    private void showPrivacyDialogOld() {
        PricacyPolicyBean result = new PricacyPolicyBean();
        result.title = "Mtime???????????????????????????";
        result.content = PrivacyKt.mTimePrivacyContent((view, key) -> {
            if (key.contains("????????????")) {
                gotoWeb(LoadManager.getRegisterPrivacyUrl());
            } else {
                gotoWeb(LoadManager.getRegisterServiceUrl());
            }
            return null;
        });
        PrivacyPolicyDialog.show(result, getSupportFragmentManager(), new PrivacyPolicyDialog.OnDismissListner() {
            @Override
            public void onDismiss(boolean isOk) {
                if (isOk) {
                    launch();
                }
            }
        });
    }

    private PrivacyDialog privacyDialog = null;

    private void showPrivacyDialog() {
        privacyDialog = new PrivacyDialog.Builder(this)
                .setTitle(PrivacyKt.getPrivacyTitle())
                .setContent(PrivacyKt.mTimePrivacyContent((view, key) -> {
                    if (key.contains("????????????")) {
                        gotoWeb(LoadManager.getRegisterPrivacyUrl());
                    } else {
                        gotoWeb(LoadManager.getRegisterServiceUrl());
                    }
                    return null;
                }))
                .setUserAgreementButton((dialog, which) -> {
                    gotoWeb(LoadManager.getRegisterServiceUrl());
                })
                .setPrivacyPolicyButton((dialog, which) -> {
                    gotoWeb(LoadManager.getRegisterPrivacyUrl());
                })
                .setPositiveButton("??????", (dialog, which) -> {
                    savePrivacyState();
                    dialog.dismiss();
                    launch();
                })
                .setNegativeButton("???????????????", (dialog, which) -> {
//                    dialog.dismiss();
//                    finish();
                    confirmation();
                })
                .create();
        privacyDialog.show();
    }

    private void confirmation() {
        if (privacyDialog != null) {
            privacyDialog.hide();
        }
        new BaseDialog.Builder(this)
                .setContent("???????????????????????????????????????????????????")
                .setPositiveButton("?????????", (dialog, which) -> {
                            dialog.dismiss();
                            if (privacyDialog != null) {
                                privacyDialog.show();
                            }
                        }
                )
                .setNegativeButton("??????", (dialog, which) -> {
                            dialog.dismiss();
                            finish();
                        }
                )
                .create()
                .show();
    }

    private void savePrivacyState() {
        putSpValue("privacy_state", true);
        putSpValue("privacy_version", getVersionCode());
    }

    private boolean isShowPrivacyDialog() {
        boolean privacyState = getSpValue("privacy_state", false);
        if (!privacyState) {
            return true;
        }
        long privacyVersion = getSpValue("privacy_version", 0L);
        return getVersionCode() > privacyVersion;
    }

    private void launch() {
        App.getInstance().getPrefsManager().putBoolean(App.getInstance().MORE_THAN_ONCE, true);
        App.getInstance().initUserPrivacyInfos();

        load();
    }

    private void gotoWeb(String url) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        Uri uri = Uri.parse(url);
        intent.setData(uri);
        startActivity(intent);
    }
}
