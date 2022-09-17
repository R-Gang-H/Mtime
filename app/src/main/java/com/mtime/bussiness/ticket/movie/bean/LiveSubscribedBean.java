package com.mtime.bussiness.ticket.movie.bean;

import com.mtime.base.bean.MBaseBean;

import java.util.List;

/**
 * Created by LEE on 2/10/17.
 */

public class LiveSubscribedBean extends MBaseBean {
    private List<LiveSubsribeItem> liveList;

    public List<LiveSubsribeItem> getLiveList() {
        return liveList;
    }

    public void setLiveList(List<LiveSubsribeItem> liveList) {
        this.liveList = liveList;
    }
}

