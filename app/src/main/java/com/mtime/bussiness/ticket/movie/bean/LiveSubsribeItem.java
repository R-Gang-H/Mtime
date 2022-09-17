package com.mtime.bussiness.ticket.movie.bean;

import com.mtime.base.bean.MBaseBean;

/**
 * Created by LEE on 2/10/17.
 */

public class LiveSubsribeItem extends MBaseBean {
    private int liveId; // 直播id
    private boolean isSubscribe; // 预约状态 true:已预约,false:没有预约

    public int getLiveId() {
        return liveId;
    }

    public void setLiveId(int liveId) {
        this.liveId = liveId;
    }

    public boolean isSubscribe() {
        return isSubscribe;
    }

    public void setSubscribe(boolean subscribe) {
        isSubscribe = subscribe;
    }
}
