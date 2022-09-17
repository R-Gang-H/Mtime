package com.mtime.bussiness.ticket.movie.bean;

import com.mtime.base.bean.MBaseBean;
import com.mtime.beans.ADTotalBean;

/**
 * Created by LEE on 7/28/16.
 */
public class ActorViewBean extends MBaseBean {
    private ActorInfoBean background;
    private ADTotalBean advertisement;

    public ActorInfoBean getBackground() {
        return background;
    }

    public void setBackground(ActorInfoBean background) {
        this.background = background;
    }

    public ADTotalBean getAdvertisement() {
        return advertisement;
    }

    public void setAdvertisement(ADTotalBean advertisement) {
        this.advertisement = advertisement;
    }
}
