package com.mtime.applink;

import com.mtime.base.statistic.StatisticConstant;
import com.mtime.base.statistic.bean.StatisticPageBean;
import com.mtime.constant.AppConstants;
import com.mtime.statistic.large.push.StatisticPush;

import java.util.HashMap;
import java.util.Map;

public enum ApplinkFrom {
    /**
     * app 内部
     */
    Internal(0),
    /**
     * Scheme_H5
     */
    Scheme_H5(1),
    /**
     * Push
     */
    Push(2),
    /**
     * Internal_H5
     */
    Internal_H5(3);
    
    private int value = 0;
    
    ApplinkFrom(int value) {
        this.value = value;
    }
    
    public int value() {
        return this.value;
    }
    
    public static String getValue(ApplinkFrom from) {
        StatisticPageBean bean = new StatisticPageBean();
        bean.pageName = StatisticPush.PAGE_PUSH;
        bean.path = new HashMap<>();
        bean.path.put(StatisticConstant.AREA1, StatisticPush.PUSH_ONE);
        Map<String,String> businessParam= new HashMap<>();
        businessParam.put(StatisticPush.PUSH_REACH_ID, AppConstants.getInstance().getPushReachId());
        bean.businessParam = businessParam;
        switch (from) {
            case Scheme_H5:
                return bean.toString();
            case Push:
                return bean.toString();
            default:
                return "";
        }
    }
}
