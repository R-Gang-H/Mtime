package com.mtime.bussiness.ticket.movie.bean;

import com.mtime.base.bean.MBaseBean;

import java.io.Serializable;

public class ActorStyle extends MBaseBean implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -4491744611432806362L;
    private int isLeadPage;
    private String leadUrl;
    private String leadImg;
    
    public String getLeadUrl() {
        return leadUrl;
    }
    public void setLeadUrl(String leadUrl) {
        this.leadUrl = leadUrl;
    }
    public String getLeadImg() {
        return leadImg;
    }
    public void setLeadImg(String leadImg) {
        this.leadImg = leadImg;
    }

    public int getIsLeadPage() {
        return isLeadPage;
    }

    public void setIsLeadPage(int isLeadPage) {
        this.isLeadPage = isLeadPage;
    }
}
