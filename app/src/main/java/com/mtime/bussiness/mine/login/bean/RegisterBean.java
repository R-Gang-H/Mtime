package com.mtime.bussiness.mine.login.bean;

import com.mtime.base.bean.MBaseBean;

/**
 * 普通返回结果
 */
public class RegisterBean  extends MBaseBean {
    private int bizCode; //0,成功 1手机已注册，2非法手机号，3图片验证码不正确 4.短信已达到今日上限
    private String giftImage;
    private UserItem userInfo;
    private String bizMsg;

    public int getBizCode() {
        return bizCode;
    }

    public void setBizCode(int bizCode) {
        this.bizCode = bizCode;
    }

    public UserItem getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserItem userInfo) {
        this.userInfo = userInfo;
    }

    public String getBizMsg() {
        return bizMsg;
    }

    public void setBizMsg(String bizMsg) {
        this.bizMsg = bizMsg;
    }

    public String getGiftImage() {
        return giftImage;
    }

    public void setGiftImage(String giftImage) {
        this.giftImage = giftImage;
    }
}