package com.jssdk.beans;

/**
 * Created by Mtime on 17/7/14.
 */

public class HandleGoBackBean {
    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    private Data data;

    public static class Data {
        private boolean isCloseWindow;// 关闭当前页面
        private boolean refreshWindow; // 刷新返回的页面
        private String test;

        public boolean isCloseWindow() {
            return isCloseWindow;
        }

        public void setCloseWindow(boolean closeWindow) {
            isCloseWindow = closeWindow;
        }

        public boolean isRefreshWindow() {
            return refreshWindow;
        }

        public void setRefreshWindow(boolean refreshWindow) {
            this.refreshWindow = refreshWindow;
        }

        public String getTest() {
            return test;
        }

        public void setTest(String test) {
            this.test = test;
        }
    }
}