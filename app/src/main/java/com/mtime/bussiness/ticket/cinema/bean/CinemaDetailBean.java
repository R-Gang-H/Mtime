package com.mtime.bussiness.ticket.cinema.bean;

import com.mtime.base.bean.MBaseBean;
import com.mtime.beans.ADTotalBean;

/**
 * Created by LEE on 7/25/16.
 */
public class CinemaDetailBean extends MBaseBean {
    private CinemaViewJsonBean baseInfo;
    private ADTotalBean advertisement;

    public CinemaViewJsonBean getBaseInfo() {
        return baseInfo;
    }

    public void setBaseInfo(CinemaViewJsonBean baseInfo) {
        this.baseInfo = baseInfo;
    }

    public ADTotalBean getAdvertisement() {
        return advertisement;
    }

    public void setAdvertisement(ADTotalBean advertisement) {
        this.advertisement = advertisement;
    }

}
