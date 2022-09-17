package com.mtime.bussiness.splash.bean;


import com.mtime.base.bean.MBaseBean;

public class SplashEntryPointBean extends MBaseBean {
    private int android;
    private boolean isForceBindMobile;//false, //第三方强制绑定手机，false不用绑定，true绑定。
    private boolean isKeyPassBindMobile;//false, //关键路径绑定手机，false不用绑定，true绑定。
    private boolean isOnlyMemberBuyTicket;//false //是否只有会员能购票，false表示非会员也能购票，true表示只有会员能购票。

    public int getAndroid() {
        return android;
    }

    public void setAndroid(int android) {
        this.android = android;
    }

    public boolean isForceBindMobile() {
        return isForceBindMobile;
    }

    public void setForceBindMobile(boolean forceBindMobile) {
        isForceBindMobile = forceBindMobile;
    }

    public boolean isKeyPassBindMobile() {
        return isKeyPassBindMobile;
    }

    public void setKeyPassBindMobile(boolean keyPassBindMobile) {
        isKeyPassBindMobile = keyPassBindMobile;
    }

    public boolean isOnlyMemberBuyTicket() {
        return isOnlyMemberBuyTicket;
    }

    public void setOnlyMemberBuyTicket(boolean onlyMemberBuyTicket) {
        isOnlyMemberBuyTicket = onlyMemberBuyTicket;
    }
}
