package com.mtime.bussiness.mine.bean;

import com.mtime.base.bean.MBaseBean;

/**
 * Created by zhulinping on 2017/6/28.
 */

public class CouponExchangeResult  extends MBaseBean {
    private int bizCode;
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

