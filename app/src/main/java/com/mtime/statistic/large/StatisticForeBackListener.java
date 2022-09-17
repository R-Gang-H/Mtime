package com.mtime.statistic.large;

import com.mtime.base.application.AppForeBackListener;
import com.mtime.base.statistic.StatisticConstant;
import com.mtime.base.statistic.app.StatisticApp;
import com.mtime.base.statistic.bean.StatisticPageBean;
import com.mtime.frame.App;
import com.mtime.util.SystemUtil;

import java.util.HashMap;

/**
 * 程序退出前后数据上报
 */
/**
 * logx统计功能废弃
 */
@Deprecated
public class StatisticForeBackListener implements AppForeBackListener {
    @Override
    public void onBecameForeground() {
        StatisticPageBean bean = new StatisticPageBean();
        bean.pageName = StatisticApp.PAGE_APP;
        bean.path = new HashMap<>();
        bean.path.put(StatisticConstant.AREA1, StatisticApp.APP_ENTER_FORE);
        StatisticManager.getInstance().submit(bean);
    }

    @Override
    public void onBecameBackground() {
        StatisticPageBean bean = new StatisticPageBean();
        bean.pageName = StatisticApp.PAGE_APP;
        bean.path = new HashMap<>();
        bean.path.put(StatisticConstant.AREA1, StatisticApp.APP_ENTER_BACK);
        StatisticManager.getInstance().submit(bean);

        //程序退出
        int appStaus = SystemUtil.getAppSatus(App.getInstance(), App.getInstance(). getPackageName());
        if (appStaus == 3) {
            StatisticManager.getInstance().clear();
        }
    }
}
