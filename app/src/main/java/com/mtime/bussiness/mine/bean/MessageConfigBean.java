package com.mtime.bussiness.mine.bean;

import com.mtime.base.bean.MBaseBean;

import java.util.List;

public class MessageConfigBean   extends MBaseBean {
    private int                 interruptionFreeStart;  // 免到打扰开始时间点，单位小时，范围0~23
    private int                 interruptionFreeEnd;    // 免打扰结束时间点，单位小时，范围1~24.如果小于开始时间认为是次日。
    private boolean             isMessage        = true;
    private boolean             isBroadcast      = true; // 消息广播提醒是否开启
    private boolean             isRemindNewMovie = true;
    private boolean             isUpdateVersion  = true;
    private boolean             isReview    = true;
    private boolean             isSwitchCity     = true;
    private String              version;
    private int                 cityId;
    private String              channelId;
    private String              unSubscribeBroadcasts;  // 推荐广播类型编号字符串
    private boolean             isLowMode        = true;
    //是否屏蔽海报
    private boolean             isFilter         = false;
    
    private List<SubscribeBean> subscribeBroadcastList; // 订阅广播列表
    
    public int getInterruptionFreeStart() {
        return interruptionFreeStart;
    }
    
    public void setInterruptionFreeStart(final int interruptionFreeStart) {
        this.interruptionFreeStart = interruptionFreeStart;
    }
    
    public int getInterruptionFreeEnd() {
        return interruptionFreeEnd;
    }
    
    public void setInterruptionFreeEnd(final int interruptionFreeEnd) {
        this.interruptionFreeEnd = interruptionFreeEnd;
    }
    
    public String getVersion() {
        return version;
    }
    
    public void setVersion(final String version) {
        this.version = version;
    }
    
    public List<SubscribeBean> getSubscribeBroadcastList() {
        return subscribeBroadcastList;
    }
    
    public void setSubscribeBroadcastList(final List<SubscribeBean> subscribeBroadcastList) {
        this.subscribeBroadcastList = subscribeBroadcastList;
    }
    
    public boolean getIsMessage() {
        return isMessage;
    }
    
    public void setIsMessage(boolean isMessage) {
        this.isMessage = isMessage;
    }
    
    public boolean getIsBroadcast() {
        return isBroadcast;
    }
    
    public void setIsBroadcast(boolean isBroadcast) {
        this.isBroadcast = isBroadcast;
    }
    
    public boolean getIsRemindNewMovie() {
        return isRemindNewMovie;
    }
    
    public void setIsRemindNewMovie(boolean isRemindNewMovie) {
        this.isRemindNewMovie = isRemindNewMovie;
    }
    
    public boolean getIsUpdateVersion() {
        return this.isUpdateVersion;
    }
    
    public void setIsUpdateVersion(boolean isUpdateVersion) {
        this.isUpdateVersion = isUpdateVersion;
    }
    
    public boolean getIsSwitchCity() {
        return isSwitchCity;
    }
    
    public void setIsSwitchCity(final boolean isSwitchCity) {
        this.isSwitchCity = isSwitchCity;
    }

    public boolean isReview() {
        return isReview;
    }

    public void setIsReview(boolean isReview) {
        this.isReview = isReview;
    }

    public int getCityId() {
        return cityId;
    }
    
    public void setCityId(int cityId) {
        this.cityId = cityId;
    }
    
    public String getChannelId() {
        return channelId;
    }
    
    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }
    
    public String getUnSubscribeBroadcasts() {
        return unSubscribeBroadcasts;
    }
    
    public void setUnSubscribeBroadcasts(String unSubscribeBroadcasts) {
        this.unSubscribeBroadcasts = unSubscribeBroadcasts;
    }
    
    public void setIsLowMode(boolean isLowMode) {
        this.isLowMode = isLowMode;
    }
    
    public boolean getIsLowMode() {
        return this.isLowMode;
    }

    public void setIsFilter(boolean isFilter){
        this.isFilter = isFilter;
    }

    public boolean getIsFilter() {
        return isFilter;
    }
}
