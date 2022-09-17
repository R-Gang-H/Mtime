package com.mtime.bussiness.ticket.cinema.bean;

import com.mtime.base.bean.MBaseBean;

/**
 * createETicketOrder接口返回的json实体
 * 
 * @author Administrator 2012-10-15
 * 
 */
public class ETicketOrderResultJsonBean extends MBaseBean {
    // {"success":true,"msg":"成功","orderId":20186509,"subOrderId":187190,"status":1}
    private boolean success;
    private String msg;
    private int status;
    private String orderId; // 订单id
    private String subOrderId; // 子订单

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

    public String getOrderId() {
	return orderId;
    }

    public void setOrderId(final String orderId) {
	this.orderId = orderId;
    }

    public String getSubOrderId() {
	return subOrderId;
    }

    public void setSubOrderId(final String subOrderId) {
	this.subOrderId = subOrderId;
    }
}
