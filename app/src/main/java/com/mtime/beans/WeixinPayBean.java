package com.mtime.beans;

import com.mtime.base.bean.MBaseBean;

public class WeixinPayBean extends MBaseBean {
    
    private String appid;
    private String noncestr;
//    private String package;
    private String partnerid;
    private String prepayid;
    private String timestamp;
    private String sign;
    public String getAppid() {
        return appid;
    }
    public void setAppid(String appid) {
        this.appid = appid;
    }
    public String getNoncestr() {
        return noncestr;
    }
    public void setNoncestr(String noncestr) {
        this.noncestr = noncestr;
    }
    public String getPartnerid() {
        return partnerid;
    }
    public void setPartnerid(String partnerid) {
        this.partnerid = partnerid;
    }
    public String getPrepayid() {
        return prepayid;
    }
    public void setPrepayid(String prepayid) {
        this.prepayid = prepayid;
    }
    public String getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
    public String getSign() {
        return sign;
    }
    public void setSign(String sign) {
        this.sign = sign;
    }
    
    
}
