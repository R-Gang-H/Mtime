package com.mtime.statistic.large;

import com.mtime.base.statistic.bean.StatisticPageBean;

/**
 * Created by <a href="mailto:wangkunlin1992@gmail.com">Wang kunlin</a>
 * <p>
 * On 2019-04-17
 */
/**
 * logx统计功能废弃
 */
@Deprecated
public class StatisticWrapBean {

    StatisticPageBean bean;

    public StatisticWrapBean(StatisticPageBean bean) {
        this.bean = bean;
    }

    @Override
    public String toString() {
        return bean.toString();
    }

    public String submit() {
        return StatisticManager.getInstance().submit(bean).toString();
    }
}
