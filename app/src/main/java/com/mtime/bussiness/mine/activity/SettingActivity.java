package com.mtime.bussiness.mine.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.collection.ArrayMap;

import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;

import com.jeremyliao.liveeventbus.LiveEventBus;
import com.kotlin.android.mtime.ktx.GlobalDimensionExt;
import com.kotlin.android.mtime.ktx.ext.ShapeExt;
import com.kotlin.android.retrofit.cookie.CookieManager;
import com.kotlin.android.app.router.liveevent.event.LoginState;
import com.kotlin.android.app.router.path.RouterActivityPath;
import com.kotlin.android.user.UserManager;
import com.kotlin.android.user.login.UserLoginKt;
import com.kotlin.android.user.login.jguang.JLoginManager;
import com.mtime.R;
import com.mtime.base.location.LocationException;
import com.mtime.base.location.LocationInfo;
import com.mtime.base.location.OnLocationCallback;
import com.mtime.base.utils.MToastUtils;
import com.mtime.bussiness.location.LocationHelper;
import com.mtime.bussiness.mine.bean.MessageConfigBean;
import com.mtime.bussiness.mine.bean.MessageConfigsSetBean;
import com.mtime.bussiness.mine.bean.StatusBean;
import com.mtime.bussiness.mine.history.dao.HistoryDao;
import com.mtime.bussiness.mine.widget.NotDisturbTimeDialog;
import com.mtime.common.cache.CacheManager;
import com.mtime.common.utils.PrefsManager;
import com.mtime.common.utils.Utils;
import com.mtime.constant.SpManager;
import com.mtime.event.EventManager;
import com.mtime.frame.App;
import com.mtime.frame.BaseActivity;
import com.mtime.mtmovie.network.ApiClient;
import com.mtime.network.ConstantUrl;
import com.mtime.network.CookiesHelper;
import com.mtime.network.RequestCallback;
import com.mtime.util.CustomAlertDlg;
import com.mtime.util.HttpUtil;
import com.mtime.util.JumpUtil;
import com.mtime.util.MtimeUtils;
import com.mtime.util.ToolsUtils;
import com.mtime.util.UIUtil;
import com.mtime.widgets.BaseTitleView.ActionType;
import com.mtime.widgets.BaseTitleView.ITitleViewLActListener;
import com.mtime.widgets.BaseTitleView.StructType;
import com.mtime.widgets.PromotionPromptView;
import com.mtime.widgets.TitleOfNormalView;

import java.io.File;
import java.util.Map;

import static com.kotlin.android.router.liveevent.EventKeyExtKt.LOGIN_STATE;

@Route(path = RouterActivityPath.AppUser.PAGE_SETTING)
public class SettingActivity extends BaseActivity {

    public static final String KEY_REMIND_NEW_MOVIE = "remind_new_movie";
    public static final String KEY_UPDATE_VER = "update_ver";
    public static final String KEY_LOW_MODE = "low_mode";
    public static final String KEY_CITYCHANGE_SET = "cityChange_set";
    public static final String KEY_COMMENTS_SET = "comments_set";
    public static final String KEY_BROADCAST_PUSH_SET = "broadcast_push_set";

    private TextView notdisturbTimeTextView = null;
    private LinearLayout cleanLayout = null;
    private View notDisturbTimeLayout = null;

    private TextView diskSize = null;
    private View goto_notice_manger;

    private View newTag;
    private TextView verCode;
    private View gotoUpdate;
    private ImageView setFiler;
    private TextView btnLogout;

    public MessageConfigBean messageConfig;
    private PrefsManager prefsManager;

    private RequestCallback getMessageConfigCallback = null;
    private RequestCallback setMessageConfigsCallback = null;

    //打开海报屏蔽
    private boolean filerOn = false;

    @Override
    protected void onInitEvent() {
        OnClickListener notDisturbTimeManage = new OnClickListener() {

            @Override
            public void onClick(View v) {
                NotDisturbTimeDialog dialog = new NotDisturbTimeDialog(SettingActivity.this,
                        R.style.SelectSeatChangeDialogStyle);
                dialog.showActionSheet();
                dialog.setOkClickListener(new NotDisturbTimeDialog.IOkClickListener() {

                    @Override
                    public void onEvent(int start, int end) {
                        if (start == 0 || end == 0) {
                            prefsManager.putInt(App.getInstance().KEY_NOTDISTURB_TIME_START, -1);
                            prefsManager.putInt(App.getInstance().KEY_NOTDISTURB_TIME_END, -1);
                            notdisturbTimeTextView.setText(SettingActivity.this.getString(R.string.st_setting_nolimit));
                        } else {
                            prefsManager.putInt(App.getInstance().KEY_NOTDISTURB_TIME_START, start - 1);
                            prefsManager.putInt(App.getInstance().KEY_NOTDISTURB_TIME_END, end);
                            notdisturbTimeTextView.setText(start - 1 + ":00--" + (start - 1 > end ? "次日" : "") + end + ":00");
                        }
                    }
                });
            }
        };

        getMessageConfigCallback = new RequestCallback() {
            @Override
            public void onSuccess(final Object o) {
                UIUtil.dismissLoadingDialog();
                messageConfig = (MessageConfigBean) o;
                if (messageConfig != null) {
                    prefsManager.putBoolean(SettingActivity.KEY_REMIND_NEW_MOVIE, messageConfig.getIsRemindNewMovie());
                    prefsManager.putBoolean(SettingActivity.KEY_UPDATE_VER, messageConfig.getIsUpdateVersion());
                    prefsManager.putBoolean(SettingActivity.KEY_CITYCHANGE_SET, messageConfig.getIsSwitchCity());
                    filerOn = messageConfig.getIsFilter();
                    prefsManager.putBoolean(App.getInstance().KEY_FILTER_SET, filerOn);
                    App.getInstance().FILTER_SET = filerOn;
                    //发送海报过滤事件
                    EventManager.getInstance().sendPosterFilterEvent();
                    if (filerOn) {
                        setFiler.setImageResource(R.drawable.switch_on);
                    }
                }
            }

            @Override
            public void onFail(final Exception e) {
                UIUtil.dismissLoadingDialog();
            }
        };

        setMessageConfigsCallback = new RequestCallback() {
            @Override
            public void onSuccess(final Object o) {
                UIUtil.dismissLoadingDialog();
                MessageConfigsSetBean messageConfigsSet = (MessageConfigsSetBean) o;
                if (messageConfigsSet != null && messageConfigsSet.getStatus() == 1) {
                    // MToastUtils.showShortToast("设置成功",
                    // Toast.LENGTH_SHORT).show();
                }
                finish();
            }

            @Override
            public void onFail(final Exception e) {
                UIUtil.dismissLoadingDialog();
                MToastUtils.showShortToast(getString(R.string.st_setting_set_failed) + ":" + e.getLocalizedMessage());
            }
        };

        OnClickListener clearClick = new OnClickListener() {
            @Override
            public void onClick(final View v) {
                clearCache();
            }
        };

        OnClickListener gotoNoticeMangeClick = new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UserManager.Companion.getInstance().isLogin()) {
                    JumpUtil.startSettingNoticeManageActivity(SettingActivity.this, "");
                } else {
                    // 跳转到登录页面（登录后需要刷新页面）
//                    SettingActivity.this.startActivityForResult(LoginActivity.class, new Intent(), 3);
                    UserLoginKt.gotoLoginPage(SettingActivity.this, null, 3);
                }
            }
        };

        OnClickListener gotoUpdateClick = new OnClickListener() {
            @Override
            public void onClick(View v) {
                downLoadNewApk();
            }
        };

        //点击海报屏蔽
        OnClickListener setFilerClick = new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (UserManager.Companion.getInstance().isLogin()) {
                    ImageView v = (ImageView) view;
                    v.setImageResource(filerOn ? R.drawable.switch_off : R.drawable.switch_on);
                    filerOn = !filerOn;
                    prefsManager.putBoolean(App.getInstance().KEY_FILTER_SET, filerOn);
                    App.getInstance().FILTER_SET = filerOn;
                    //发送获取海报过滤通知
                    EventManager.getInstance().sendPosterFilterEvent();
                } else {
                    // 跳转到登录页面（登录后需要刷新页面）
//                    SettingActivity.this.startActivityForResult(LoginActivity.class, new Intent(), 3);
                    UserLoginKt.gotoLoginPage(SettingActivity.this, null, 3);
                }
            }
        };

        //退出登录
        OnClickListener logoutClick = new OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        };

        // -------------------------------------------------------------------------------------------------------------
        diskSize.setText(CacheManager.getInstance().getFileCacheSize());

        // -------------------------------------------------------------------------------------------------------------
        notDisturbTimeLayout.setOnClickListener(notDisturbTimeManage);

        cleanLayout.setOnClickListener(clearClick);

        goto_notice_manger.setOnClickListener(gotoNoticeMangeClick);
        gotoUpdate.setOnClickListener(gotoUpdateClick);
        setFiler.setOnClickListener(setFilerClick);
        btnLogout.setOnClickListener(logoutClick);

    }

    /**
     * 取出清除缓存二次确认弹框
     */
    private void clearCache() {
        final Handler mHandler = new Handler();
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                CacheManager.getInstance().cleanAllFileCache();
                CookiesHelper.clearWebViewCache();
                CookiesHelper.clearCookies();
                CookiesHelper.initialize = true;
                File cacheDir = new File(getCacheDir(), "volley");
                if (cacheDir != null && cacheDir.isDirectory()) {
                    File[] fileList = cacheDir.listFiles();
                    for (int i = 0; i < fileList.length; i++) {
                        fileList[i].delete();
                    }
                }

                //新网络框架清除cache方法
                ApiClient.SingletonBuilder.clearCache();

                //图片框架清除cache
                volleyImageLoader.clearAll(SettingActivity.this);

                // 清除历史记录
                HistoryDao.clearAll();

                diskSize.setText(CacheManager.getInstance().getFileCacheSize());

                final CustomAlertDlg dlgMessage = new CustomAlertDlg(SettingActivity.this,
                        CustomAlertDlg.TYPE_OK);
                dlgMessage.setBtnOKListener(new OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        dlgMessage.dismiss();
                    }
                });
                dlgMessage.show();
                dlgMessage.setText(SettingActivity.this.getString(R.string.st_setting_clear_success));
            }
        });
    }

    private void logout() {
        final CustomAlertDlg dlg = new CustomAlertDlg(this, CustomAlertDlg.TYPE_OK_CANCEL);
        dlg.setBtnCancelListener(new OnClickListener() {
            public void onClick(final View v) {
                dlg.dismiss();
            }
        });
        dlg.setBtnOKListener(new OnClickListener() {
            public void onClick(final View v) {
                dlg.dismiss();
                UIUtil.showLoadingDialog(SettingActivity.this);
                HttpUtil.post(ConstantUrl.POST_LOGOUT, StatusBean.class, new RequestCallback() {
                    public void onSuccess(final Object o) {
                        UIUtil.dismissLoadingDialog();
                        StatusBean successBean = (StatusBean) o;
                        if (successBean.isSuccess()) {
                            // 清空未读消息
                            App.getInstance().UNREADMESSAGEBEAN = null;
                            // 清除任何与用户有关的信息
//                            NetworkManager.getInstance().mCookieJarManager.clear();
                            CookieManager.Companion.getInstance().clear();
//                            AccountManager.removeAccountInfo();
                            UserManager.Companion.getInstance().clear();
                            SpManager.clearUsageRecord();
                            PromotionPromptView.removeNextRequestTime(SettingActivity.this);

                            CacheManager.getInstance().cleanAllFileCache();
                            CookiesHelper.clearWebViewCache();
                            CookiesHelper.clearCookies();
                            CookiesHelper.initialize = true;
                            File cacheDir = new File(SettingActivity.this.getCacheDir(), "volley");
                            if (cacheDir != null && cacheDir.isDirectory()) {
                                File[] fileList = cacheDir.listFiles();
                                for (int i = 0; i < fileList.length; i++) {
                                    fileList[i].delete();
                                }
                            }

                            String pushtoken = ToolsUtils.getToken(SettingActivity.this.getApplicationContext());
                            String jpushid = ToolsUtils.getJPushId(SettingActivity.this.getApplicationContext());

                            if (!TextUtils.isEmpty(pushtoken) || !TextUtils.isEmpty(jpushid)) {
                                Map<String, String> parameterList = new ArrayMap<String, String>(3);
                                parameterList.put("deviceToken", pushtoken);
                                parameterList.put("setMessageConfigType", String.valueOf(2));
                                parameterList.put("jPushRegId", jpushid);
                                HttpUtil.post(ConstantUrl.SET_MESSAGECONFIGS, parameterList, MessageConfigsSetBean.class, null);
                            }
                            MToastUtils.showShortToast(R.string.str_logout_success);
                            JLoginManager.Companion.getInstance().prepare();
                            btnLogout.setVisibility(View.INVISIBLE);
                            LiveEventBus.get(LOGIN_STATE).post(new LoginState(false));
                            EventManager.getInstance().sendLogoutEvent();
                            finish();
                        } else {
                            MToastUtils.showShortToast("登出失败");
                        }
                    }

                    @Override
                    public void onFail(final Exception e) {
                        UIUtil.dismissLoadingDialog();
                        String tip = "退出登录失败" + (TextUtils.isEmpty(e.getLocalizedMessage()) ? "" : ":" + e.getLocalizedMessage());
                        MToastUtils.showShortToast(tip);
                    }
                });

            }
        });
        dlg.show();
        dlg.setText(SettingActivity.this.getString(R.string.str_logout_confrim));
    }


    @Override
    protected void onInitVariable() {
        prefsManager = App.getInstance().getPrefsManager();
        messageConfig = getCurrentConfig();
        setPageLabel("basicSetting");
    }

    @Override
    protected void onInitView(final Bundle savedInstanceState) {
        this.setContentView(R.layout.activity_settings);
        View navBar = this.findViewById(R.id.navigationbar);
        new TitleOfNormalView(this, navBar, StructType.TYPE_NORMAL_SHOW_BACK_TITLE, this.getResources().getString(
                R.string.str_settings), getBackClickListener());

        notDisturbTimeLayout = findViewById(R.id.notdisturb_time_set);
        notdisturbTimeTextView = findViewById(R.id.notdisturb_time_textview);
        if (getDisturbBeginTime() == -1) {
            notdisturbTimeTextView.setText(getString(R.string.st_setting_nolimit));
        } else {
            int start = getDisturbBeginTime();
            int end = getDisturbEndTime();
            notdisturbTimeTextView.setText(start + ":00--" + (start > end ? "次日" : "") + end + ":00");
        }
        goto_notice_manger = findViewById(R.id.goto_notice_manage);
        setFiler = findViewById(R.id.set_filer);
        // 版本更新
        newTag = findViewById(R.id.new_tag);
        verCode = findViewById(R.id.version_code);
        gotoUpdate = findViewById(R.id.goto_update_version);
        // 清缓存
        cleanLayout = findViewById(R.id.clean);
        diskSize = findViewById(R.id.size);
        //退出登录
        btnLogout = findViewById(R.id.logout);
        btnLogout.setVisibility(UserManager.Companion.getInstance().isLogin() ? View.VISIBLE : View.GONE);
        ShapeExt.INSTANCE.setShapeColorAndCorner(btnLogout, R.color.color_ff5a36, 44);
    }

    private int getDisturbEndTime() {
        return prefsManager.getInt(App.getInstance().KEY_NOTDISTURB_TIME_END, -1);
    }

    private int getDisturbBeginTime() {
        return prefsManager.getInt(App.getInstance().KEY_NOTDISTURB_TIME_START, -1);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (KeyEvent.KEYCODE_BACK == keyCode) {
            this.saveMessageConfigs();
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    protected void onLoadData() {
    }

    @Override
    protected void onRequestData() {
        initConfigData();
        initUpdateVersion();
    }

    private void initConfigData() {
        UIUtil.showLoadingDialog(this);
        HttpUtil.get(ConstantUrl.GET_MESSAGECONFIGESBYDEVICE, MessageConfigBean.class, getMessageConfigCallback);
    }

    private void initUpdateVersion() {
        // 每次获取view的时候,更新一下版本信息
        if (App.getInstance().updateVersion != null
                && Utils.compareVersion(App.getInstance().updateVersion.getVersion(), Utils.getVersionName(SettingActivity.this))) {
            verCode.setText(App.getInstance().updateVersion.getVersion());
            newTag.setVisibility(View.VISIBLE);
        } else {
            verCode.setText(R.string.str_no_new_ver);
            newTag.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onUnloadData() {
    }

    private ITitleViewLActListener getBackClickListener() {
        return new ITitleViewLActListener() {
            @Override
            public void onEvent(ActionType type, String content) {
                saveMessageConfigs();
            }
        };
    }

    private void saveMessageConfigs() {
        MessageConfigBean currentConfig = getCurrentConfig();
        if (isChangeConfig(currentConfig)) {
            UIUtil.showLoadingDialog(this);
            final Map<String, String> parameterList = new ArrayMap<String, String>(10);
            parameterList.put("deviceToken", ToolsUtils.getToken(getApplicationContext()));
            parameterList.put("setMessageConfigType", String.valueOf(4));
            parameterList.put("isRemindNewMovie", String.valueOf(prefsManager.getBoolean(KEY_REMIND_NEW_MOVIE)));
            parameterList.put("isUpdateVersion", String.valueOf(prefsManager.getBoolean(KEY_UPDATE_VER)));
            parameterList.put("isSwitchCity", String.valueOf(prefsManager.getBoolean(KEY_CITYCHANGE_SET)));
            // 本页面控制的变量，上面的都是迫于接口原因才上传的，本页面不关心上面值的改变，从PrefsManager获取即可
            parameterList.put("interruptionFreeStart", String.valueOf(getDisturbBeginTime()));
            parameterList.put("interruptionFreeEnd", String.valueOf(getDisturbEndTime()));
            parameterList.put("isFilter", String.valueOf(false));
            parameterList.put("jPushRegId", ToolsUtils.getJPushId(getApplicationContext()));

            LocationHelper.location(getApplicationContext(), new OnLocationCallback() {
                @Override
                public void onLocationSuccess(LocationInfo locationInfo) {
                    if (null != locationInfo) {
                        parameterList.put("locationId", locationInfo.getCityId());
                    } else {
                        parameterList.put("locationId", String.valueOf(GlobalDimensionExt.CITY_ID));
                    }
                    HttpUtil.post(ConstantUrl.SET_MESSAGECONFIGS, parameterList, MessageConfigsSetBean.class, setMessageConfigsCallback);
                }

                @Override
                public void onLocationFailure(LocationException e) {
                    onLocationSuccess(LocationHelper.getDefaultLocationInfo());
                }
            });
        }
    }

    private Boolean isChangeConfig(MessageConfigBean currentConfig) {
        Boolean result = true;
        if (messageConfig != null && currentConfig != null) {
            result = currentConfig.getIsFilter() != messageConfig.getIsFilter()
                    || currentConfig.getInterruptionFreeEnd() != messageConfig.getInterruptionFreeEnd()
                    || currentConfig.getInterruptionFreeStart() != messageConfig.getInterruptionFreeStart();
        }
        return result;
    }

    // 获取当前配置
    private MessageConfigBean getCurrentConfig() {
        MessageConfigBean config = new MessageConfigBean();

        config.setInterruptionFreeStart(getDisturbBeginTime());
        config.setInterruptionFreeEnd(getDisturbEndTime());
        config.setIsFilter(prefsManager.getBoolean(App.getInstance().KEY_FILTER_SET, false));

        return config;
    }

    /* 下载apk安装包 */
    private void downLoadNewApk() {
        if ((App.getInstance().updateVersion != null) && (App.getInstance().updateVersion.getUrl() != null)) {
            MtimeUtils.downLoadApk(this);
            MToastUtils.showShortToast(getString(R.string.st_setting_down_tips));
        } else {
            MToastUtils.showShortToast(getString(R.string.st_setting_version_tips));
        }
    }

    @Override
    protected void onActivityResult(int arg0, int arg1, Intent arg2) {
        if (arg1 == App.STATUS_LOGIN && 3 == arg0) {
            // 登录后需要更新数据
            initConfigData();
            btnLogout.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 自己定义refer
     *
     * @param context
     * @param refer
     */
    public static void launch(Context context, String refer) {
        Intent launcher = new Intent(context, SettingActivity.class);
        dealRefer(context, refer, launcher);
        context.startActivity(launcher);
    }

    @Override
    protected boolean openEventBus() {
        return true;
    }
}
