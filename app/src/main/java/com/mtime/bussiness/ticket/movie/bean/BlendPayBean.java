package com.mtime.bussiness.ticket.movie.bean;


import com.mtime.base.bean.MBaseBean;

public class BlendPayBean extends MBaseBean {
    private boolean success;
    private String msg;
    private int status;
    private String formXML;
    private String rechargeNumber;
    private String unionPayMsg;
    private String activityMsg;
    private boolean isPromotionCount;
    private boolean isUserLimitMAX;
    public String getFormXML() {
        return formXML;
    }

    public void setFormXML(String formXML) {
        this.formXML = formXML;
    }

    public String getRechargeNumber() {
        return rechargeNumber;
    }

    public void setRechargeNumber(String rechargeNumber) {
        this.rechargeNumber = rechargeNumber;
    }

    public boolean isSuccess() {
    return success;
    }

    public void setSuccess(final boolean success) {
    this.success = success;
    }

    public String getMsg() {
    return msg;
    }

    public void setMsg(final String msg) {
    this.msg = msg;
    }

    public int getStatus() {
    return status;
    }

    public void setStatus(final int status) {
    this.status = status;
    }

    public String getUnionPayMsg() {
        return unionPayMsg;
    }

    public void setUnionPayMsg(String unionPayMsg) {
        this.unionPayMsg = unionPayMsg;
    }
    public String getActivityMsg() {
        return activityMsg;
    }

    public void setActivityMsg(String activityMsg) {
        this.activityMsg = activityMsg;
    }

    public boolean isPromotionCount() {
        return isPromotionCount;
    }

    public void setIsPromotionCount(boolean isPromotionCount) {
        this.isPromotionCount = isPromotionCount;
    }

    public boolean isUserLimitMAX() {
        return isUserLimitMAX;
    }

    public void setIsUserLimitMAX(boolean isUserLimitMAX) {
        this.isUserLimitMAX = isUserLimitMAX;
    }
}