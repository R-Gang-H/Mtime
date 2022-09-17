package com.mtime.bussiness.mine.login.bean;

import com.mtime.base.bean.MBaseBean;
import com.mtime.bussiness.mine.bean.ImageCodeItem;

/**
 * Created by LEE on 12/19/16.
 */

public class SmsCodeBean extends MBaseBean {
    private int bizCode; //0,正常发送。1手机已注册，2非法手机号，3图片验证码不正确 4.短信已达到今日上限
    private String bizMsg;
    private String smsCodeId;  //短信验证码id
    private ImageCodeItem imgCode; //  图片验证码

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

    public ImageCodeItem getImgCode() {
        return imgCode;
    }

    public void setImgCode(ImageCodeItem imgCode) {
        this.imgCode = imgCode;
    }

    public String getBizMsg() {
        return bizMsg;
    }

    public void setBizMsg(String bizMsg) {
        this.bizMsg = bizMsg;
    }
}
