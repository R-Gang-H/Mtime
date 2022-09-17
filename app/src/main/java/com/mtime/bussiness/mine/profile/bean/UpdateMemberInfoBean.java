package com.mtime.bussiness.mine.profile.bean;

import com.mtime.base.bean.MBaseBean;

/**
 * Created by vivian.wei on 2017/4/7.
 * 修改会员基本信息（生日或居住地）Bean
 */

public class UpdateMemberInfoBean  extends MBaseBean {
    private int bizCode;                // 0-成功
    private String bizMsg;
    private String birthday;            // 生日 2008-08-08
    private String locationRelation;    // 居住地层级关系  国家id-省id-城市id

    public int getBizCode() {
        return bizCode;
    }

    public void setBizCode(int bizCode) {
        this.bizCode = bizCode;
    }

    public String getBizMsg() {
        return bizMsg;
    }

    public void setBizMsg(String bizMsg) {
        this.bizMsg = bizMsg;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getLocationRelation() {
        return locationRelation;
    }

    public void setLocationRelation(String locationRelation) {
        this.locationRelation = locationRelation;
    }
}
