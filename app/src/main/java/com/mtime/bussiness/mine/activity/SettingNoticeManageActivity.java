package com.mtime.bussiness.mine.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.collection.ArrayMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.kotlin.android.mtime.ktx.GlobalDimensionExt;
import com.mtime.bussiness.common.utils.MSharePreferenceHelper;
import com.mtime.bussiness.splash.SplashNewActivity;
import com.mtime.constant.SpManager;
import com.mtime.frame.App;
import com.mtime.frame.BaseActivity;
import com.mtime.R;
import com.mtime.base.location.LocationException;
import com.mtime.base.location.LocationInfo;
import com.mtime.base.location.OnLocationCallback;
import com.mtime.base.utils.MToastUtils;
import com.mtime.bussiness.location.LocationHelper;
import com.mtime.bussiness.mine.bean.MessageConfigBean;
import com.mtime.bussiness.mine.bean.MessageConfigsSetBean;
import com.mtime.util.HttpUtil;
import com.mtime.network.RequestCallback;
import com.mtime.common.utils.PrefsManager;
import com.mtime.widgets.BaseTitleView.ActionType;
import com.mtime.widgets.BaseTitleView.ITitleViewLActListener;
import com.mtime.widgets.BaseTitleView.StructType;
import com.mtime.widgets.TitleOfNormalView;
import com.mtime.network.ConstantUrl;
import com.mtime.util.ToolsUtils;
import com.mtime.util.UIUtil;

import java.util.Map;

import cn.jpush.android.api.JPushInterface;

public class SettingNoticeManageActivity extends BaseActivity {
    public MessageConfigBean messageConfig;

//    private CheckBox newCheck = null;
    private CheckBox checkUpdate = null;
    private CheckBox cityChangeCheck;
//    private CheckBox commentsCheck = null;
//    private CheckBox broadcastPushCheck;

//    private TextView commentsText = null;
//    private TextView tvBroadcastPush;
    private TextView updateVer = null;
    private TextView cityChangeSet = null;
//    private TextView newMovie = null;

    private ViewGroup mPushCheckLayout;
    private CheckBox mPushCheck;

    private ViewGroup mPersonalityRecommendCheckLayout;
    private CheckBox mPersonalityRecommendCheck;

    private PrefsManager prefsManager;

    private RequestCallback getMessageConfigCallback = null;
    private RequestCallback setMessageConfigsCallback = null;

    @Override
    protected void onInitEvent() {

//        OnClickListener clickComments= new OnClickListener() {
//            @Override
//            public void onClick(final View v) {
//                commentsCheck.setChecked(!commentsCheck.isChecked());
//            }
//        };
//
//        OnClickListener clickBroadcastPush = new OnClickListener() {
//            @Override
//            public void onClick(final View v) {
//                broadcastPushCheck.setChecked(!broadcastPushCheck.isChecked());
//            }
//        };

        OnClickListener cityChangeSetClick = new OnClickListener() {
            @Override
            public void onClick(final View v) {
                cityChangeCheck.setChecked(!cityChangeCheck.isChecked());
            }
        };
//        OnClickListener clickRemind = new OnClickListener() {
//            @Override
//            public void onClick(final View v) {
//                newCheck.setChecked(!newCheck.isChecked());
//            }
//        };
        OnClickListener updateClick = new OnClickListener() {
            @Override
            public void onClick(final View v) {
                checkUpdate.setChecked(!checkUpdate.isChecked());
            }
        };

//        OnCheckedChangeListener commentsChange = new OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
//                setCheckBoxView((CheckBox) buttonView, isChecked);
//                prefsManager.putBoolean(SettingActivity.KEY_COMMENTS_SET, isChecked);
//            }
//        };

//        OnCheckedChangeListener broadcastPushChange = new OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
//                setCheckBoxView((CheckBox) buttonView, isChecked);
//                prefsManager.putBoolean(SettingActivity.KEY_BROADCAST_PUSH_SET, isChecked);
//            }
//        };
//
        OnCheckedChangeListener cityChange = new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
                setCheckBoxView((CheckBox) buttonView, isChecked);
                prefsManager.putBoolean(SettingActivity.KEY_CITYCHANGE_SET, isChecked);
            }
        };
//        OnCheckedChangeListener remindChange = new OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
//                setCheckBoxView((CheckBox) buttonView, isChecked);
//                prefsManager.putBoolean(SettingActivity.KEY_REMIND_NEW_MOVIE, isChecked);
//            }
//        };
//
        OnCheckedChangeListener updateChange = new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
                prefsManager.putBoolean(SettingActivity.KEY_UPDATE_VER, isChecked);
                setCheckBoxView((CheckBox) buttonView, isChecked);
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
                    prefsManager.putBoolean(SettingActivity.KEY_COMMENTS_SET, messageConfig.isReview());
                    prefsManager.putBoolean(SettingActivity.KEY_BROADCAST_PUSH_SET, messageConfig.getIsBroadcast());
                    setCheckBoxViews(messageConfig);
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
                MessageConfigsSetBean messageConfigsSet = (MessageConfigsSetBean) o;
                if (messageConfigsSet != null && messageConfigsSet.getStatus() == 1) {
                    MToastUtils.showShortToast("设置成功");
                }
                finish();
            }

            @Override
            public void onFail(final Exception e) {
                MToastUtils.showShortToast("设置失败");
            }
        };

        setCheckBoxViews(messageConfig);
        // -------------------------------------------------------------------------------------------------------------


//        commentsText.setOnClickListener(clickComments);
//        commentsCheck.setOnCheckedChangeListener(commentsChange);

//        tvBroadcastPush.setOnClickListener(clickBroadcastPush);
//        broadcastPushCheck.setOnCheckedChangeListener(broadcastPushChange);

        cityChangeSet.setOnClickListener(cityChangeSetClick);
        cityChangeCheck.setOnCheckedChangeListener(cityChange);

//        newMovie.setOnClickListener(clickRemind);
//        newCheck.setOnCheckedChangeListener(remindChange);

        updateVer.setOnClickListener(updateClick);
        checkUpdate.setOnCheckedChangeListener(updateChange);

        mPushCheckLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setPushChanged(!mPushCheck.isChecked());
            }
        });
        mPushCheck.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setPushChanged(isChecked);
            }
        });

        mPersonalityRecommendCheckLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setPersonalityRecommendChanged(!mPersonalityRecommendCheck.isChecked());
            }
        });
        mPersonalityRecommendCheck.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setPersonalityRecommendChanged(isChecked);
            }
        });
    }

    private void setPersonalityRecommendChanged(boolean isChecked) {
        setCheckBoxView(mPersonalityRecommendCheck, isChecked);
        MSharePreferenceHelper.get().putBoolean(SpManager.SP_KEY_PERSONALITY_RECOMMEND, isChecked);
        //重启
        Intent intent = new Intent(this, SplashNewActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void setPushChanged(boolean isChecked) {
        setCheckBoxView(mPushCheck, isChecked);
        if (mPushCheck.isChecked()) {
            JPushInterface.resumePush(getApplicationContext());
        } else {
            JPushInterface.stopPush(getApplicationContext());
        }
    }

    @Override
    protected void onInitVariable() {
        prefsManager = App.getInstance().getPrefsManager();
        messageConfig = getConfigByPrefsManager();
        setPageLabel("noticeManager");
    }

    @Override
    protected void onInitView(final Bundle savedInstanceState) {
        this.setContentView(R.layout.act_my_setting_notice_manage);
        View navBar = this.findViewById(R.id.navigationbar);
        new TitleOfNormalView(this, navBar, StructType.TYPE_NORMAL_SHOW_BACK_TITLE, this.getResources().getString(
                R.string.str_notice_manage), getBackClickListener());

//        commentsText = (TextView) findViewById(R.id.comment_set);
//        tvBroadcastPush = (TextView) findViewById(R.id.tv_broadcast_push);
//        newMovie = (TextView) findViewById(R.id.new_moive);
        updateVer = findViewById(R.id.version);
        cityChangeSet = findViewById(R.id.city_change);

//        commentsCheck = (CheckBox) findViewById(R.id.comment_check);
//        broadcastPushCheck = (CheckBox) findViewById(R.id.broadcast_push_check);
//        newCheck = (CheckBox) findViewById(R.id.remind_check);
        checkUpdate = findViewById(R.id.update_check);
        cityChangeCheck = findViewById(R.id.city_change_check);

        mPushCheckLayout = findViewById(R.id.push_check_layout);
        mPushCheck = findViewById(R.id.push_check);
        setCheckBoxView(mPushCheck, !JPushInterface.isPushStopped(getApplicationContext()));

        mPersonalityRecommendCheckLayout = findViewById(R.id.personality_recommend_check_layout);
        mPersonalityRecommendCheck = findViewById(R.id.personality_recommend_check);
        setCheckBoxView(mPersonalityRecommendCheck, MSharePreferenceHelper.get().getBooleanValue(SpManager.SP_KEY_PERSONALITY_RECOMMEND, true));
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
        UIUtil.showLoadingDialog(this);
        HttpUtil.get(ConstantUrl.GET_MESSAGECONFIGESBYDEVICE, MessageConfigBean.class, getMessageConfigCallback);
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
            final Map<String, String> parameterList = new ArrayMap<String, String>(12);
            parameterList.put("deviceToken", ToolsUtils.getToken(getApplicationContext()));
            parameterList.put("setMessageConfigType", String.valueOf(4));
            parameterList.put("interruptionFreeStart", String.valueOf(getDisturbBeginTime()));
            parameterList.put("interruptionFreeEnd", String.valueOf(getDisturbEndTime()));
            parameterList.put("isFilter", String.valueOf(prefsManager.getBoolean(App.getInstance().KEY_FILTER_SET)));
            parameterList.put("isReview", String.valueOf(currentConfig.isReview()));
            parameterList.put("isBroadcast", String.valueOf(currentConfig.getIsBroadcast()));
            parameterList.put("isRemindNewMovie", String.valueOf(currentConfig.getIsRemindNewMovie()));
            parameterList.put("isUpdateVersion", String.valueOf(currentConfig.getIsUpdateVersion()));
            parameterList.put("isSwitchCity", String.valueOf(currentConfig.getIsSwitchCity()));
            parameterList.put("jPushRegId", ToolsUtils.getJPushId(getApplicationContext()));
    
            LocationHelper.location(getApplicationContext(), new OnLocationCallback() {
                @Override
                public void onLocationSuccess(LocationInfo locationInfo) {
                    if(null != locationInfo) {
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
            result =  (currentConfig.isReview() != messageConfig.isReview())
                    || (currentConfig.getIsBroadcast() != messageConfig.getIsBroadcast())
                    || (currentConfig.getIsRemindNewMovie() != messageConfig.getIsRemindNewMovie())
                    || (currentConfig.getIsUpdateVersion() != messageConfig.getIsUpdateVersion())
                    || (currentConfig.getIsSwitchCity() != messageConfig.getIsSwitchCity());
        }
        return result;
    }

    // 获取当前配置
    private MessageConfigBean getCurrentConfig() {
        MessageConfigBean config = new MessageConfigBean();

        // 评论回复
        config.setIsReview(prefsManager.getBoolean(SettingActivity.KEY_COMMENTS_SET));
        // 广播推送
        config.setIsBroadcast(prefsManager.getBoolean(SettingActivity.KEY_BROADCAST_PUSH_SET));
        // 系统通知
        config.setIsRemindNewMovie(prefsManager.getBoolean(SettingActivity.KEY_REMIND_NEW_MOVIE));
        config.setIsUpdateVersion(prefsManager.getBoolean(SettingActivity.KEY_UPDATE_VER));
        config.setIsSwitchCity(prefsManager.getBoolean(SettingActivity.KEY_CITYCHANGE_SET));

        return config;
    }

    private int getDisturbEndTime() {
        return prefsManager.getInt(App.getInstance().KEY_NOTDISTURB_TIME_END, -1);
    }

    private int getDisturbBeginTime() {
        return prefsManager.getInt(App.getInstance().KEY_NOTDISTURB_TIME_START, -1);
    }

    // 获取设置信息
    private MessageConfigBean getConfigByPrefsManager() {
        MessageConfigBean config = new MessageConfigBean();

        config.setIsReview(prefsManager.getBoolean(SettingActivity.KEY_COMMENTS_SET, true));
        config.setIsBroadcast(prefsManager.getBoolean(SettingActivity.KEY_BROADCAST_PUSH_SET, true));
        config.setIsRemindNewMovie(prefsManager.getBoolean(SettingActivity.KEY_REMIND_NEW_MOVIE, true));
        config.setIsSwitchCity(prefsManager.getBoolean(SettingActivity.KEY_CITYCHANGE_SET, true));
        config.setIsLowMode(prefsManager.getBoolean(SettingActivity.KEY_LOW_MODE, true));
        config.setIsUpdateVersion(prefsManager.getBoolean(SettingActivity.KEY_UPDATE_VER, true));

        return config;
    }

    private void setCheckBoxViews(MessageConfigBean config) {
//        if (commentsCheck.isChecked() != config.isReview())
//            setCheckBoxView(commentsCheck, config.isReview());
//
//        if (broadcastPushCheck.isChecked() != config.getIsBroadcast()) {
//            setCheckBoxView(broadcastPushCheck, config.getIsBroadcast());
//        }

        if (checkUpdate.isChecked() != config.getIsUpdateVersion())
            setCheckBoxView(checkUpdate, config.getIsUpdateVersion());

        if (cityChangeCheck.isChecked() != config.getIsSwitchCity())
            setCheckBoxView(cityChangeCheck, config.getIsSwitchCity());

//        if (newCheck.isChecked() != config.getIsRemindNewMovie())
//            setCheckBoxView(newCheck, config.getIsRemindNewMovie());
    }

    private void setCheckBoxView(CheckBox box, boolean isCheck) {
        box.setChecked(isCheck);
        if (isCheck) {
            box.setBackgroundResource(R.drawable.check_on);
        } else {
            box.setBackgroundResource(R.drawable.check_off);
        }
    }

    /**
     * 自己定义refer
     * @param context
     * @param refer
     */
    public static void launch(Context context, String refer) {
        Intent launcher = new Intent(context, SettingNoticeManageActivity.class);
        dealRefer(context,refer, launcher);
        context.startActivity(launcher);
    }
}
