package com.mtime.bussiness.mine.profile.bean;

import com.mtime.base.bean.MBaseBean;

/**
 * Created by LEE on 12/22/16.
 */

public class RegetPasswordVeryCodeBean  extends MBaseBean {
    private int bizCode; //是否发送成功-0, 验证失败--1；服务异常 -2
    private String message;  //发送失败错误提示
    private String token;   ////一个验证的凭证

    public int getBizCode() {
        return bizCode;
    }

    public void setBizCode(int bizCode) {
        this.bizCode = bizCode;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
