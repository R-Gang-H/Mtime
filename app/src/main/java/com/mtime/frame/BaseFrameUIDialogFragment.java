package com.mtime.frame;

import android.os.Bundle;

import com.mtime.base.dialog.BaseDialogFragment;
import com.mtime.base.statistic.StatisticConstant;
import com.mtime.base.statistic.StatisticDataBuild;
import com.mtime.base.statistic.bean.StatisticPageBean;
import com.mtime.event.entity.EmptyEvent;
import com.mtime.statistic.large.StatisticManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by <a href="mailto:wangkunlin1992@gmail.com">Wang kunlin</a>
 * <p>
 * On 2018-06-14
 */
public abstract class BaseFrameUIDialogFragment extends BaseDialogFragment {

    protected BaseStatisticHelper mBaseStatisticHelper = new BaseStatisticHelper(false);

    @Override
    public void onResume() {
        super.onResume();

        mBaseStatisticHelper.onResume(getContext());
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

        mBaseStatisticHelper.onPause(getContext());
    }

    /**
     * 组装统计类
     */
    public StatisticPageBean assemble(String firstRegion, String firstRegionMark, String secRegion,
                                      String secRegionMark, String thrRegion, String thrRegionMark, Map<String, String> businessParam) {
        return StatisticDataBuild.assemble(mBaseStatisticHelper.getLastPageRefer(), mBaseStatisticHelper.getPageLabel(), firstRegion,
                firstRegionMark, secRegion, secRegionMark, thrRegion, thrRegionMark, businessParam);
    }

    public StatisticPageBean assemble() {
        return StatisticDataBuild.assemble(mBaseStatisticHelper.getLastPageRefer(), mBaseStatisticHelper.getPageLabel(), null,
                null, null, null, null, null, null);
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
