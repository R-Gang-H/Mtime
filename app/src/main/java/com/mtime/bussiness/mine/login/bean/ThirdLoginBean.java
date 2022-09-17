package com.mtime.bussiness.mine.login.bean;

/**
 * Created by LEE on 12/16/16.
 */

public class ThirdLoginBean extends LoginBaseItem {
    private int platformId;             //1 新浪微博、2 qq、4微信
    private String token;      //授权口令,用于绑定手机，创建用户是授权的凭证。

    public int getPlatformId() {
        return platformId;
    }

    public void setPlatformId(int platformId) {
        this.platformId = platformId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
