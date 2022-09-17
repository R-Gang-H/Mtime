package com.mtime.frame;

import android.os.Bundle;

import com.kk.taurus.uiframe.f.StateFragment;
import com.kk.taurus.uiframe.v.BaseErrorHolder;
import com.kk.taurus.uiframe.v.BaseLoadingHolder;
import com.kk.taurus.uiframe.v.ContentHolder;
import com.mtime.base.statistic.StatisticDataBuild;
import com.mtime.base.statistic.bean.StatisticPageBean;
import com.mtime.event.entity.EmptyEvent;
import com.mtime.frame.holder.ErrorHolder;
import com.mtime.frame.holder.LoadingHolder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Map;

/**
 * Created by mtime on 2017/10/10.
 */

public abstract class BaseFrameUIFragment<T, H extends ContentHolder<T>> extends StateFragment<T,H> {

    protected BaseStatisticHelper mBaseStatisticHelper = new BaseStatisticHelper(false);

    public void setRefer(String refer){
        mBaseStatisticHelper.setLastPageRefer(refer);
    }

    public String getRefer() {
        return mBaseStatisticHelper.getLastPageRefer();
    }

    public String getPageLabel() {
        return mBaseStatisticHelper.getPageLabel();
    }

    public void setPageLabel(String pn) {
        mBaseStatisticHelper.setPageLabel(pn);
        mBaseStatisticHelper.setSubmit(true);
    }

    public void getParentFragmentPageLabelAndRefer() {
        //作用子页面，直接取父页面的PN和refer
        if(getParentFragment() instanceof BaseFrameUIFragment) {
            mBaseStatisticHelper.setPageLabel(((BaseFrameUIFragment) getParentFragment()).getPageLabel());
            mBaseStatisticHelper.setLastPageRefer(((BaseFrameUIFragment) getParentFragment()).getRefer());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //页面基础埋点-open
        mBaseStatisticHelper.onResume(getApplicationContext());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (openEventBus()) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        //页面基础埋点-close/timing
        mBaseStatisticHelper.onPause(getApplicationContext());
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
        return StatisticDataBuild.assemble(mBaseStatisticHelper.getLastPageRefer(), mBaseStatisticHelper.getPageLabel(),firstRegion, firstRegionMark, secRegion, secRegionMark, thrRegion, thrRegionMark, businessParam);
    }
    
    public StatisticPageBean assemble() {
        return StatisticDataBuild.assemble(mBaseStatisticHelper.getLastPageRefer(), mBaseStatisticHelper.getPageLabel(),null, null, null, null, null, null, null);
    }

    @Override
    public void onHolderEvent(int eventCode, Bundle bundle) {
        super.onHolderEvent(eventCode, bundle);
        switch (eventCode){
            case BaseErrorHolder.ERROR_EVENT_ON_ERROR_CLICK:
                onErrorRetry();
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (openEventBus()) {
            EventBus.getDefault().unregister(this);
        }
    }

    /**
     * true表示打开eventbus
     * @return false
     */
    protected boolean openEventBus() {
        return false;
    }

    @Subscribe
    public void empty(EmptyEvent event) {

    }


    protected void onErrorRetry(){

    }

    @Override
    public ContentHolder onBindContentHolder() {
        return null;
    }

    @Override
    public BaseLoadingHolder onBindLoadingHolder() {
        return new LoadingHolder(mContext);
    }

    @Override
    public BaseErrorHolder onBindErrorHolder() {
        return new ErrorHolder(mContext);
    }

}
