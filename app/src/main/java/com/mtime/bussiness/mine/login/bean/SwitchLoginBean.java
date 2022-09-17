package com.mtime.bussiness.mine.login.bean;

import com.mtime.base.bean.MBaseBean;
import com.mtime.bussiness.mine.bean.AccountLocationBean;

/**
 * 切换登录bean
 *
 * Created by vivian.wei on 15/9/1.
 *
 */

public class SwitchLoginBean  extends MBaseBean {

    private boolean success;
    private String error;
    private int userId;
    private int userLevel;
    private String nickname;
    private String headPic;
    private String mobile;
    private int sex;
    private int balance;
    private boolean isVirtualUser;
    private boolean hasBindedMobile; // true:需要， false:不需要
    private String birthday;
    private AccountLocationBean location; // 居住地

    public boolean getSuccess() {
        return success;
    }

    public void setSuccess (boolean success) {
        this.success = success;
    }

    public String getError() {
        return error;
    }

    public void setError (String error) {
        this.error = error;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId (int userId) {
        this.userId = userId;
    }

    public int getUserLevel() {
        return userLevel;
    }

    public void setUserLevel (int userLevel) {
        this.userLevel = userLevel;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname (String nickname) {
        this.nickname = nickname;
    }

    public String getHeadPic() {
        return headPic;
    }

    public void setHeadPic (String headPic) {
        this.headPic = headPic;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile (String mobile) {
        this.mobile = mobile;
    }

    public int getSex() {
        return sex;
    }

    public void setSex (int sex) {
        this.sex = sex;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance (int balance) {
        this.balance = balance;
    }

    public boolean getVirtualUser () {
        return isVirtualUser;
    }

    public void setVirtualUser (boolean isVirtualUser) {
        this.isVirtualUser = isVirtualUser;
    }

    public boolean isHasBindedMobile() {
        return hasBindedMobile;
    }

    public void setHasBindedMobile(boolean hasBindedMobile) {
        this.hasBindedMobile = hasBindedMobile;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public AccountLocationBean getLocation() {
        return location;
    }

    public void setLocation(AccountLocationBean location) {
        this.location = location;
    }
}
