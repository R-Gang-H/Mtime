package com.mtime.bussiness.ticket.movie.bean;

import com.mtime.base.bean.MBaseBean;

/**
 * Created by yinguanping on 17/2/23.
 */

public class RechargeBean  extends MBaseBean {
    private boolean success;
    private String message;
    private int status;
    private String error;
    private String rechargeNum;
    private String fromXML;
    private long orderId;
    private long subOrderId;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(final boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(final int status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(final String error) {
        this.error = error;
    }

    public String getRechargeNum() {
        return rechargeNum;
    }

    public void setRechargeNum(final String rechargeNum) {
        this.rechargeNum = rechargeNum;
    }

    public String getFromXML() {
        return fromXML;
    }

    public void setFromXML(final String fromXML) {
        this.fromXML = fromXML;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(final long orderId) {
        this.orderId = orderId;
    }

    public long getSubOrderId() {
        return subOrderId;
    }

    public void setSubOrderId(final long subOrderId) {
        this.subOrderId = subOrderId;
    }
}

