package com.mtime.bussiness.ticket.movie.bean;

import com.mtime.base.bean.MBaseBean;

/**
 * Created by LEE on 12/20/16.
 * 绑定手机号时使用的短信验证码格式, 和注册时的短信验证码还不一样。
 */

public class SmsVeryCodeBean extends MBaseBean {
    private int bizCode; //0 成功，-1验证码发送失败　-2新绑定手机号码已经被占用　 -3 用户请求非法　-4请求验证码次数超过限制 需要图片验证码校验
                        // //-5  手机号已注册，请使用其他手机号绑定  -6 第三方账号未注册，验证的手机号没有绑定过同类第三方账号（弹窗）
    private String smsCodeId;  //短信验证码id
    private String imgCodeId; ////图片验证码id
    private String imgCodeUrl; //图片验证码url
    private String mobileToken; ////设置密码时，从此token获取注册的手机号
    private String message;  ////提示

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

    public String getImgCodeUrl() {
        return imgCodeUrl;
    }

    public void setImgCodeUrl(String imgCodeUrl) {
        this.imgCodeUrl = imgCodeUrl;
    }

    public String getMobileToken() {
        return mobileToken;
    }

    public void setMobileToken(String mobileToken) {
        this.mobileToken = mobileToken;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
