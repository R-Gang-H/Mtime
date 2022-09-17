package com.jssdk.beans;

/**
 * Created by yinguanping on 2017/5/25.
 */

public class OpenAppLinkBean extends CommonBean {

    private DataBean data;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        private String applinkData;
        public String originUrl; // 埋点用的字段，目前传原始URL，iOS10.5.0 & 6.5.0 开始支持
        
        public String getApplinkData() {
            return applinkData;
        }

        public void setApplinkData(String applinkData) {
            this.applinkData = applinkData;
        }

    }
}