package com.mtime.bussiness.ticket.movie.bean;

import com.mtime.base.bean.MBaseBean;

/**
 * 轮询子订单时获取的json实体
 */
public class SubOrderStatusJsonBean extends MBaseBean {
    // 子订单Id
    private long orderId;
    // 子订单状态
    private int subOrderStatus;

    public long getOrderId() {
	return orderId;
    }

    public void setOrderId(final long orderId) {
	this.orderId = orderId;
    }

    public int getSubOrderStatus() {
	return subOrderStatus;
    }

    public void setSubOrderStatus(final int subOrderStatus) {
	this.subOrderStatus = subOrderStatus;
    }
}
