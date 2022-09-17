package com.mtime.bussiness.mine.bean;

import com.mtime.base.bean.MBaseBean;

/**
 * Created by vivian.wei on 2017/5/16.
 */

public class PublicCancelFollowBean  extends MBaseBean {

    private int bizCode; //1:成功；0:失败
    private String bizMsg;

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

}
