package com.mtime.bussiness.mine.login.bean;

import com.mtime.base.bean.MBaseBean;

/**
 * Created by LEE on 12/16/16.
 */

public class UserItem  extends MBaseBean {
    protected String nickname; //"昵称"
    protected int userId; // user id
    protected String headImg;  //"头像地址"
    protected String mobile;  //绑定手机号
    protected int gender;  ////性别 1 男 、2 女、 3 保密

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getHeadImg() {
        return headImg;
    }

    public void setHeadImg(String headImg) {
        this.headImg = headImg;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }
}
