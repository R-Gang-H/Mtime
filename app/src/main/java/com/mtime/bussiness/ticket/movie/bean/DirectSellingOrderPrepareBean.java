package com.mtime.bussiness.ticket.movie.bean;

import com.mtime.base.bean.MBaseBean;

/**
 * Created by zhuqiguang on 2018/5/28.
 * website www.zhuqiguang.cn
 */
public class DirectSellingOrderPrepareBean extends MBaseBean {
    public static final String BIZ_CODE_SUCCESS = "1";
    private String jumpUrl;

    public String getJumpUrl() {
        return jumpUrl;
    }

    public void setJumpUrl(String jumpUrl) {
        this.jumpUrl = jumpUrl;
    }

    public String getBizCode() {
        return bizCode;
    }

    public void setBizCode(String bizCode) {
        this.bizCode = bizCode;
    }

    public String getBizMsg() {
        return bizMsg;
    }

    public void setBizMsg(String bizMsg) {
        this.bizMsg = bizMsg;
    }

    private String bizCode; //1代表正常生成URL。1以外的任何值，说明由于业务原因（比如该场次已经下线）导致无法跳转
    private String bizMsg;//该场次已经下线
}
