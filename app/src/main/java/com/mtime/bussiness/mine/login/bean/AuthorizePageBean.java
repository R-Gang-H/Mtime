package com.mtime.bussiness.mine.login.bean;

import com.mtime.base.bean.MBaseBean;

public class AuthorizePageBean  extends MBaseBean {

    private int type;
    private String appId;
    private String oauthUrl;
    
    public int getType() {
        return type;
    }
    public void setType(int type) {
        this.type = type;
    }
    public String getAppId() {
        return appId;
    }
    public void setAppId(String appId) {
        this.appId = appId;
    }
    public String getOauthUrl() {
        return oauthUrl;
    }
    public void setOauthUrl(String oauthUrl) {
        this.oauthUrl = oauthUrl;
    }
    
    
}
