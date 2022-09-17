package com.jssdk.beans;

/**
 * Created by vivian.wei on 2017/7/14.
 * 显示生日/等级礼包弹窗
 */

public class ShowBirthdayLevelPopPageBean {
    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    private Data data;

    public static class Data {
        private String type;    // 0生日，1等级

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }
}