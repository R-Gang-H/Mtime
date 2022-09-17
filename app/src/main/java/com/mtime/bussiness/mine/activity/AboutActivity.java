package com.mtime.bussiness.mine.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.kotlin.android.app.router.path.RouterActivityPath;
import com.kotlin.android.app.router.path.RouterProviderPath;
import com.mtime.BuildConfig;
import com.mtime.R;
import com.mtime.base.utils.MToastUtils;
import com.mtime.bussiness.splash.LoadManager;
import com.mtime.constant.FrameConstant;
import com.mtime.frame.BaseActivity;
import com.mtime.network.ConstantUrl;
import com.mtime.statistic.large.h5.StatisticH5;
import com.mtime.util.JumpUtil;
import com.mtime.widgets.BaseTitleView.ActionType;
import com.mtime.widgets.BaseTitleView.ITitleViewLActListener;
import com.mtime.widgets.BaseTitleView.StructType;
import com.mtime.widgets.TitleOfNormalView;

import java.util.Calendar;

@Route(path = RouterActivityPath.AppUser.PAGE_ABOUT)
public class AboutActivity extends BaseActivity implements ITitleViewLActListener, OnClickListener {

    private View mRoot;
    private View mTicketHelpLl;
    private TextView mCopyrightTv;

    @Override
    protected void onInitEvent() {

    }

    @Override
    protected boolean enableSliding() {
        return true;
    }

    @Override
    protected void onInitVariable() {
        this.setSwipeBack(true);
        setPageLabel("aboutUs");
    }

    @Override
    protected void onInitView(final Bundle savedInstanceState) {
        this.setContentView(R.layout.act_about);

        mRoot = this.findViewById(R.id.about_title);
        mTicketHelpLl = findViewById(R.id.act_about_ticket_help_ll);
        mCopyrightTv = this.findViewById(R.id.copyright);

        // title
        /**
         * @author zl
         * @date 2020/9/21 4:42 下午
         * @desc TYPE_NORMAL_SHOW_BACK_TITLE_SHARE 暂时去掉这里的分享，新需求里没有这个页面分享
         */
        new TitleOfNormalView(this, mRoot, StructType.TYPE_NORMAL_SHOW_BACK_TITLE,
                "关于我们", this);

        // 点击mtime图标
        {
            final int COUNTS = 5;
            final long DURATION = 3 * 1000;
            long[] mHits = new long[COUNTS];
            this.findViewById(R.id.about_icon).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (BuildConfig.DEBUG) {
                        System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
                        mHits[mHits.length - 1] = SystemClock.uptimeMillis();
                        if (mHits[0] >= (SystemClock.uptimeMillis() - DURATION)) {
                            PersonalLiveSettingActivity.launch(AboutActivity.this);
                        }
                    }
                }
            });
        }

        // 版本号
        PackageInfo info;
        try {
            TextView version = findViewById(R.id.about_version);
            info = this.getPackageManager().getPackageInfo(this.getPackageName(), 0);
            String value = this.getResources().getString(R.string.st_version);
            value = String.format(value, info.versionName);
            version.setText(value);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }

        // 底部版权
        StringBuffer sb = new StringBuffer();
        sb.append(getResources().getString(R.string.copyright));
        sb.append("-");
        sb.append(Calendar.getInstance().get(Calendar.YEAR));
        mCopyrightTv.setText(sb.toString());

        mTicketHelpLl.setOnClickListener(this);
        mCopyrightTv.setOnClickListener(this);
        findViewById(R.id.act_about_mtime_rule1_ll).setOnClickListener(this);
        findViewById(R.id.act_about_mtime_rule2_ll).setOnClickListener(this);
        // todo 测试为了测试4G下直播添加入口
//        findViewById(R.id.about_version).setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                ILiveProvider provider = (ILiveProvider) ProviderFactoryKt.getProvider(ILiveProvider.class);
//                if (provider != null) {
//                    provider.launchLiveTestEntrance();
//                }
//                return true;
//            }
//        });
    }

    @Override
    protected void onLoadData() {
    }

    @Override
    protected void onRequestData() {
    }

    protected void onUnloadData() {
    }

    @Override
    public void onEvent(ActionType type, String content) {
        if(ActionType.TYPE_BACK == type) {
            // 返回
            finish();
        }
    }

    /**
     * 自己定义refer
     *
     * @param context
     * @param refer
     */
    public static void launch(Context context, String refer) {
        Intent launcher = new Intent(context, AboutActivity.class);
        dealRefer(context, refer, launcher);
        context.startActivity(launcher);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.act_about_ticket_help_ll: // 购票帮助中心
                JumpUtil.startCommonWebActivity(AboutActivity.this,
                        ConstantUrl.FEATURE_TICKET_HELP_URL, StatisticH5.PN_H5, null,
                        true, true, true, false, null);
                break;
            case R.id.copyright: // Toast渠道
                MToastUtils.showShortToast(FrameConstant.CHANNEL_ID);
                break;

            case R.id.act_about_mtime_rule1_ll:
                JumpUtil.startCommonWebActivity(this, LoadManager.getRegisterServiceUrl(), StatisticH5.PN_H5, null,
                        true, true, true, false, null);
                break;

            case R.id.act_about_mtime_rule2_ll:
                JumpUtil.startCommonWebActivity(this, LoadManager.getRegisterPrivacyUrl(), StatisticH5.PN_H5, null,
                        true, true, true, false, null);
                break;
        }
    }
}
