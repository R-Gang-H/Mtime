package com.mtime.beans;

import com.mtime.base.bean.MBaseBean;

import java.io.Serializable;

public class BoughtTicketListBean extends MBaseBean implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private int id;
    public int buyCount;
    private long lastBuyTime;

    public int getId() {
	return id;
    }

    public void setId(final int id) {
	this.id = id;
    }

    public int getBuyCount() {
	return buyCount;
    }

    public void setBuyCount(final int buyCount) {
	this.buyCount = buyCount;
    }

    public long getLastBuyTime() {
	return lastBuyTime;
    }

    public void setLastBuyTime(final long lastBuyTime) {
	this.lastBuyTime = lastBuyTime;
    }
}
