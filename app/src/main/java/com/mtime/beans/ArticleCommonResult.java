package com.mtime.beans;

import com.mtime.base.bean.MBaseBean;

/**
 * Created by yinguanping on 2017/5/9.
 */

public class ArticleCommonResult  extends MBaseBean {

    /**
     * bizCode : 1
     * bizMsg : 添加成功/取消成功
     */

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
