package com.mtime.bussiness.mine.profile.bean;

import com.mtime.base.bean.MBaseBean;

/**
 * Created by LEE on 12/21/16.
 */

public class BindMobileResultBean  extends MBaseBean {
    private int bizCode; ////0 绑定成功　-1失败
    private String message; //提示
    private String token; // 返回的token

    public int getBizCode() {
        return bizCode;
    }

    public void setBizCode(int bizCode) {
        this.bizCode = bizCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
