package com.mtime.bussiness.ticket.bean;

import com.mtime.base.bean.MBaseBean;

/**
 * @author vivian.wei
 * @date 2020/9/16
 * @desc 类描述
 */
public class CollectResultBean extends MBaseBean {
    public static final long SUCCESS = 0L;

    private long bizCode = -1;  // 业务返回码 0:成功
    private String bizMsg;      // 业务返回消息

    public long getBizCode() {
        return bizCode;
    }

    public void setBizCode(long bizCode) {
        this.bizCode = bizCode;
    }

    public String getBizMsg() {
        return bizMsg;
    }

    public void setBizMsg(String bizMsg) {
        this.bizMsg = bizMsg;
    }
}
