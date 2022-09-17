package com.mtime.frame;

import java.util.Map;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import com.kk.taurus.uiframe.a.TitleBarActivity;
import com.kk.taurus.uiframe.i.ITitleBar;
import com.kk.taurus.uiframe.v.BaseErrorHolder;
import com.kk.taurus.uiframe.v.BaseLoadingHolder;
import com.kk.taurus.uiframe.v.BaseTitleBarHolder;
import com.kk.taurus.uiframe.v.ContentHolder;
import com.kotlin.android.audio.floatview.component.aduiofloat.FloatingView;
import com.mtime.base.statistic.bean.StatisticPageBean;
import com.mtime.event.entity.EmptyEvent;
import com.mtime.frame.holder.DefaultTitleBarHolder;
import com.mtime.frame.holder.ErrorHolder;
import com.mtime.frame.holder.LoadingHolder;
import com.mtime.statistic.large.StatisticWrapBean;
import com.mtime.statusbar.StatusBarUtil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.Nullable;

/**
 * Created by Taurus on 2017/10/9.
 */

public abstract class BaseFrameUIActivity<T, H extends ContentHolder<T>> extends TitleBarActivity<T, H> {

    public static void startActivity(Context context, Intent intent, int requestCode) {
        if (context instanceof Activity) {
            ((Activity) context).startActivityForResult(intent, requestCode);
        } else {
            context.startActivity(intent);
        }
    }

    /********** 数据埋点上报相关 ******************/

    //页面基础埋点帮忙类
    protected BaseStatisticHelper mBaseStatisticHelper = new BaseStatisticHelper();

    /**
     * 统一处理refer，如果有自定义refer已自定义为准，例如push，如果没有并且是Activity的话，则上个页面的mLastPageRefer为准
     *
     * @param context
     * @param refer
     * @param launcher
     */
    public static void dealRefer(Context context, String refer, Intent launcher) {
        BaseStatisticHelper.dealRefer(context, refer, launcher);
    }

    /**
     * 组装统计类（用于无统计，方便生成refer）
     */
    public StatisticWrapBean assemble() {
        return assemble1(null, null, null, null, null, null, null);
    }

    /**
     * 组装统计类
     *
     * @param firstRegion
     * @param firstRegionMark
     * @param secRegion
     * @param secRegionMark
     * @param thrRegion
     * @param thrRegionMark
     * @param businessParam
     * @return
     */
    public StatisticPageBean assemble(String firstRegion, String firstRegionMark, String secRegion,
                                      String secRegionMark, String thrRegion, String thrRegionMark, Map<String, String> businessParam) {
        return mBaseStatisticHelper.assemble(firstRegion, firstRegionMark, secRegion,
                secRegionMark, thrRegion, thrRegionMark, businessParam);
    }

    private StatisticWrapBean assemble1(String firstRegion, String firstRegionMark, String secRegion,
                                        String secRegionMark, String thrRegion, String thrRegionMark, Map<String, String> businessParam) {
        return mBaseStatisticHelper.assemble1(firstRegion, firstRegionMark, secRegion, secRegionMark, thrRegion, thrRegionMark, businessParam);
    }

    public StatisticWrapBean assemble(Map<String, String> param, String... rms) {
        if (rms == null || rms.length == 0) {
            return assemble1(null, null, null, null, null, null, param);
        }
        switch (rms.length) {
            case 1:
                return assemble1(rms[0], null, null, null, null, null, param);
            case 2:
                return assemble1(rms[0], rms[1], null, null, null, null, param);
            case 3:
                return assemble1(rms[0], rms[1], rms[2], null, null, null, param);
            case 4:
                return assemble1(rms[0], rms[1], rms[2], rms[3], null, null, param);
            case 5:
                return assemble1(rms[0], rms[1], rms[2], rms[3], rms[4], null, param);
            case 6:
            default:
                return assemble1(rms[0], rms[1], rms[2], rms[3], rms[4], rms[5], param);
        }
    }

    public StatisticWrapBean assembleOnlyRegion(Map<String, String> param, String... regions) {
        if (regions == null || regions.length == 0) {
            return assemble1(null, null, null, null, null, null, param);
        }
        String first = regions[0];
        String second = null;
        String third = null;
        if (regions.length >= 2) {
            second = regions[1];
        }
        if (regions.length >= 3) {
            third = regions[2];
        }
        return assemble1(first, null, second, null, third, null, param);
    }

    public String getRefer() {
        return mBaseStatisticHelper.getLastPageRefer();
    }

    public void setSubmit(boolean submit) {
        mBaseStatisticHelper.setSubmit(submit);
    }

    public boolean isSubmit() {
        return mBaseStatisticHelper.isSubmit();
    }

    public void setPageLabel(String pageLabel) {
        mBaseStatisticHelper.setPageLabel(pageLabel);
    }

    public String getPageLabel() {
        return mBaseStatisticHelper.getPageLabel();
    }

    public void setBaseStatisticParam(Map<String, String> baseParam) {
        mBaseStatisticHelper.setBaseParam(baseParam);
    }

    public Map<String, String> getBaseStatisticParam() {
        return mBaseStatisticHelper.getBaseParam();
    }

    public void putBaseStatisticParam(String key, String value) {
        mBaseStatisticHelper.putBaseParam(key, value);
    }

    /********** 数据埋点上报相关 end ******************/

    @Override
    public BaseTitleBarHolder onBindTitleBarHolder() {
        return new DefaultTitleBarHolder(this);
    }

    @Override
    public BaseLoadingHolder onBindLoadingHolder() {
        return new LoadingHolder(this);
    }

    @Override
    public BaseErrorHolder onBindErrorHolder() {
        return new ErrorHolder(this);
    }

    @Override
    public void onHolderEvent(int eventCode, Bundle bundle) {
        super.onHolderEvent(eventCode, bundle);
        switch (eventCode) {
            case ITitleBar.TITLE_BAR_EVENT_NAVIGATION_CLICK:
                onFinish();
                break;
            case BaseErrorHolder.ERROR_EVENT_ON_ERROR_CLICK:
                onErrorRetry();
                break;
        }
    }

    protected void onErrorRetry() {

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // 设置系统状态栏黑字白底 (当前默认的UI风格)
        if(StatusBarUtil.setStatusBarDarkTheme(this, true)) {
            StatusBarUtil.setStatusBarColor(this, Color.WHITE);
        }

        //页面基础埋点帮忙类
        mBaseStatisticHelper.setLastPageRefer(getIntent());

        super.onCreate(savedInstanceState);
        if (openEventBus()) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        //页面基础埋点-open
        mBaseStatisticHelper.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        //页面基础埋点-close/timing
        mBaseStatisticHelper.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (openEventBus()) {
            EventBus.getDefault().unregister(this);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FloatingView.get().attach(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        FloatingView.get().detach(this);
    }

    /**
     * true表示打开eventbus
     *
     * @return false
     */
    protected boolean openEventBus() {
        return false;
    }

    @Subscribe
    public void empty(EmptyEvent event) {

    }
}
