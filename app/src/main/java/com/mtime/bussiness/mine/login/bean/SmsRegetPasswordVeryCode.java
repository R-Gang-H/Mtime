package com.mtime.bussiness.mine.login.bean;

import com.mtime.base.bean.MBaseBean;

/**
 * Created by LEE on 12/22/16.
 * 找回密码时使用的短信验证数据对象
 */

public class SmsRegetPasswordVeryCode  extends MBaseBean {
    private int bizCode; ////0 成功，-1验证码发送失败　-3 账号不存在　 -2 图片验证错误
    private String smsCodeId;  ////短信验证码id
    private String imgCodeUrl; ////图片验证码url
    private String imgCodeId; //图片验证码code ID
    private String message;  // 提示

    // 发现接口实际返回的是imgCode，为了兼容之前的版本，多加一个字段
    private String imgCode; //图片验证码code ID

    public int getBizCode() {
        return bizCode;
    }

    public void setBizCode(int bizCode) {
        this.bizCode = bizCode;
    }

    public String getSmsCodeId() {
        return smsCodeId;
    }

    public void setSmsCodeId(String smsCodeId) {
        this.smsCodeId = smsCodeId;
    }

    public String getImgCodeId() {
        return imgCodeId;
    }

    public void setImgCodeId(String imgCodeId) {
        this.imgCodeId = imgCodeId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getImgCodeUrl() {
        return imgCodeUrl;
    }

    public void setImgCodeUrl(String imgCodeUrl) {
        this.imgCodeUrl = imgCodeUrl;
    }

    public String getImgCode() {
        return imgCode;
    }

    public void setImgCode(String imgCode) {
        this.imgCode = imgCode;
    }
}
