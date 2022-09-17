package com.mtime.bussiness.mine.login.bean;

import com.mtime.base.bean.MBaseBean;

/**
 * Created by LEE on 12/16/16.
 */

public class LoginBaseItem  extends MBaseBean {

    protected String msg; //消息
    protected String serviceEmail;  ////客服邮箱
    protected int status; // 状态 //1 登录成功、2 账号或密码有误，请重试、3 该账号已被禁止登录、4 请输入图片验证码
    protected String skipBindMobileText;  //跳过强制绑定手机号，如果没有文案就是强制绑定。
    protected UserItem user;
    protected boolean hasPassword;
    private boolean needBindMobile; // 是否需要绑定手机号

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getServiceEmail() {
        return serviceEmail;
    }

    public void setServiceEmail(String serviceEmail) {
        this.serviceEmail = serviceEmail;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getSkipBindMobileText() {
        return skipBindMobileText;
    }

    public void setSkipBindMobileText(String skipBindMobileText) {
        this.skipBindMobileText = skipBindMobileText;
    }

    public UserItem getUser() {
        return user;
    }

    public void setUser(UserItem user) {
        this.user = user;
    }

    public boolean isHasPassword() {
        return hasPassword;
    }

    public void setHasPassword(boolean hasPassword) {
        this.hasPassword = hasPassword;
    }

    public boolean isNeedBindMobile() {
        return needBindMobile;
    }

    public void setNeedBindMobile(boolean needBindMobile) {
        this.needBindMobile = needBindMobile;
    }
}
