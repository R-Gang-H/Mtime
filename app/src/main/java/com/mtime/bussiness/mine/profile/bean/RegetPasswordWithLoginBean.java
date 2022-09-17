package com.mtime.bussiness.mine.profile.bean;

import com.mtime.base.bean.MBaseBean;
import com.mtime.bussiness.mine.login.bean.UserItem;

/**
 * Created by LEE on 12/22/16.
 * 接口组应该把常用的和比较基础的统一命名起来。这app端对象创建的
 */

public class RegetPasswordWithLoginBean  extends MBaseBean {
    private int bizCode; ////是否保存成功 0  成功    1  失败  -1  令牌无效
    private String message; //修改失败原因
    private UserItem userInfo; //用户信息

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

    public UserItem getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserItem userInfo) {
        this.userInfo = userInfo;
    }
}
