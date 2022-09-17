package com.mtime.constant;

import com.mtime.statistic.large.StatisticManager;

/**
 * Created by LiJiaZhi on 2017/9/7.
 * 应用常量
 */

public class AppConstants {
    private static volatile AppConstants instance;

    protected AppConstants() {
    }

    public static AppConstants getInstance() {
        if (instance == null) {
            synchronized (AppConstants.class) {
                if (instance == null) {
                    instance = new AppConstants();
                }
            }
        }
        return instance;
    }

    // 消息推送reach_Id（用于原生页面数据上报和通过cookie传递给h5）
    private String pushReachId = "";

    //手机信息--运营商名称
    public String MOBILE_OPE;

    public void setPushReachId(String pushReachId) {
        this.pushReachId = pushReachId;
        StatisticManager.getInstance().setReachId(pushReachId);
    }

    public String getPushReachId() {
        return pushReachId;
    }
}
