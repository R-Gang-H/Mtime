package com.mtime.bussiness.ticket.movie.bean;

import com.mtime.base.bean.MBaseBean;

/**
 * 在线选座--用户最后一条未支付在线订单
 * 
 * @author ye
 */
public class WithoutPayOnlineSeat extends MBaseBean {
    private String orderId; // 主订单
    private String subOrderID; // 子订单
    private int status;
    private String orderMsg;
    public String getOrderId() {
	return orderId;
    }

    public void setOrderId(final String orderId) {
	this.orderId = orderId;
    }

    public String getSubOrderID() {
	return subOrderID;
    }

    public void setSubOrderID(final String subOrderID) {
	this.subOrderID = subOrderID;
    }

    public String getOrderMsg()
    {
        return orderMsg;
    }

    public void setOrderMsg(String orderMsg)
    {
        this.orderMsg = orderMsg;
    }

    public int getStatus()
    {
        return status;
    }

    public void setStatus(int status)
    {
        this.status = status;
    }
}
