package com.mtime.bussiness.mine.bean;

import android.text.TextUtils;

import com.mtime.base.bean.MBaseBean;

public class SubscribeBean  extends MBaseBean {
    private int broadcastType;          // 广播类型值
    private String  broadcastTypeName;  // 广播类型名称
    private boolean isSubscribe;        // 是否订阅，true订阅 false取消订阅
    
    public int getBroadcastType() {
        return broadcastType;
    }
    
    public void setBroadcastType(int broadcastType) {
        this.broadcastType = broadcastType;
    }
    
    public String getBroadcastTypeName() {
        if (TextUtils.isEmpty(broadcastTypeName)) {
            return "";
        }
        else {
            return broadcastTypeName;
        }
    }
    
    public void setBroadcastTypeName(String broadcastTypeName) {
        this.broadcastTypeName = broadcastTypeName;
    }
    
    public boolean getIsSubscribe() {
        return isSubscribe;
    }
    
    public boolean setIsSubscribe(boolean isSubscribe) {
        return this.isSubscribe = isSubscribe;
    }
    
}
