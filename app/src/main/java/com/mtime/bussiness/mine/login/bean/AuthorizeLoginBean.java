package com.mtime.bussiness.mine.login.bean;

import com.mtime.base.bean.MBaseBean;

public class AuthorizeLoginBean  extends MBaseBean {
    private UserItem user; // 用户信息
    private int     status;       // //1 登录成功、2 授权登录失败、3 授权值不能为空
    private String msg; // 提示信息
    private boolean hasPassword;

    public int getStatus() {
        return status;
    }
    
    public void setStatus(int status) {
        this.status = status;
    }

    public UserItem getUser() {
        return user;
    }

    public void setUser(UserItem user) {
        this.user = user;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public boolean isHasPassword() {
        return hasPassword;
    }

    public void setHasPassword(boolean hasPassword) {
        this.hasPassword = hasPassword;
    }
}
