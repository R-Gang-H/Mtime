package com.mtime.bussiness.ticket.movie.bean;

import com.mtime.base.bean.MBaseBean;

/**
 * 主订单状态信息
 */
public class OrderStatusJsonBean extends MBaseBean {
    private long orderId; // 订单id
    private int orderStatus; // 主订单状态
    private int payStatus; // 支付状态；0—未支付，1—已支付
    //上面三个字段是购票和后产品都有的
    private int businessType; // 订单业务类型；1—在线选座，2—电子券
    private boolean isReSelectSeat; // 是否需要重新选座
    private long reSelectSeatEndTime; // 重新选座截止时间（单位：秒）,如果不需要重新选座，不返回该字段
    //上面三个字段是购票的
    private int depositPayStatus;
    private int finalPayStatus;
    private int presellPaymentMode;
    private String orderSuccessUrl;
    //上面四个字段是后产品的

    public long getOrderId() {
	return orderId;
    }

    public void setOrderId(final long orderId) {
	this.orderId = orderId;
    }

    public int getOrderStatus() {
	return orderStatus;
    }

    public void setOrderStatus(final int orderStatus) {
	this.orderStatus = orderStatus;
    }

    public int getPayStatus() {
	return payStatus;
    }

    public void setPayStatus(final int payStatus) {
	this.payStatus = payStatus;
    }

    public int getBusinessType() {
	return businessType;
    }

    public void setBusinessType(final int businessType) {
	this.businessType = businessType;
    }

    public boolean isReSelectSeat() {
	return isReSelectSeat;
    }

    public void setReSelectSeat(final boolean isReSelectSeat) {
	this.isReSelectSeat = isReSelectSeat;
    }

    public long getReSelectSeatEndTime() {
	// String dateStr = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new
	// Date(reSelectSeatEndTime));
	return reSelectSeatEndTime;
    }

    public void setReSelectSeatEndTime(final long reSelectSeatEndTime) {
	this.reSelectSeatEndTime = reSelectSeatEndTime;
    }

    public int getDepositPayStatus() {
        return depositPayStatus;
    }

    public void setDepositPayStatus(int depositPayStatus) {
        this.depositPayStatus = depositPayStatus;
    }

    public int getFinalPayStatus() {
        return finalPayStatus;
    }

    public void setFinalPayStatus(int finalPayStatus) {
        this.finalPayStatus = finalPayStatus;
    }

    public int getPresellPaymentMode() {
        return presellPaymentMode;
    }

    public void setPresellPaymentMode(int presellPaymentMode) {
        this.presellPaymentMode = presellPaymentMode;
    }

    public String getOrderSuccessUrl() {
        if (orderSuccessUrl == null) {
            return "";
        }
        return orderSuccessUrl;
    }

    public void setOrderSuccessUrl(String orderSuccessUrl) {
        this.orderSuccessUrl = orderSuccessUrl;
    }
}
