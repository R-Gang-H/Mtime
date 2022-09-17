package com.mtime.bussiness.ticket.movie.bean;

import com.mtime.base.bean.MBaseBean;

public class FailCommodityIdBean extends MBaseBean {
    private int commodityId;
    private int errorCode;
    public int getCommodityId() {
        return commodityId;
    }
    public void setCommodityId(int commodityId) {
        this.commodityId = commodityId;
    }
    public int getErrorCode() {
        return errorCode;
    }
    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }
    
}
