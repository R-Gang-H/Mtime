package com.mtime.bussiness.ticket.movie.bean;

import com.mtime.base.bean.MBaseBean;

/**
 * 取消订单时获取的json实体
 */
public class CancelOrderJsonBean extends MBaseBean {
    // 下订单是否成功
    private boolean success;
    // 下订单提示的信息
    private String msg;
    // 下订单的状态
    private int status;

    public boolean isSuccess() {
	return success;
    }

    public void setSuccess(final boolean success) {
	this.success = success;
    }

    public String getMsg() {
	return msg;
    }

    public void setMsg(final String msg) {
	this.msg = msg;
    }

    public int getStatus() {
	return status;
    }

    public void setStatus(final int status) {
	this.status = status;
    }
}
