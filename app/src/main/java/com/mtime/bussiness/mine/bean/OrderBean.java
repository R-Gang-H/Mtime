package com.mtime.bussiness.mine.bean;

import com.mtime.base.bean.MBaseBean;

import java.io.Serializable;

public class OrderBean  extends MBaseBean implements Serializable {

    private static final long serialVersionUID = -3905824772334601851L;
    private String orderId;
    private float salesAmount;
    private int orderType;
    private float deductedAmount; // 抵扣金额
    private String description;
    private String showtimeId; // 即之前的dId（场次id）
    private String subOrderId;
    private long payEndTime;
    private long createTime;
    private int orderStatus;
    private boolean isReSelectSeat; // 是否需要重新选座
    private long reSelectSeatEndTimeSecond; // 重新选座截止时间（单位：秒）,如果不需要重新选座，不返回该字段
    private int refundStatus;// 退款状态，0-未退款，1-申请退款，2-已退款
    private String ticketCount;// 购票数量
    private String ticketName;// 订单名称

    public String getTicketName() {
        return ticketName;
    }

    public void setTicketName(String ticketName) {
        this.ticketName = ticketName;
    }

    public void setTicketCount(String ticketCount) {
        this.ticketCount = ticketCount;
    }

    public String getTicketCount() {
        return ticketCount;
    }

    public int getRefundStatus() {
        return refundStatus;
    }

    public void setRefundStatus(int refundStatus) {
        this.refundStatus = refundStatus;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(final String orderId) {
        this.orderId = orderId;
    }

    public float getSalesAmount() {
        return salesAmount;
    }

    public void setSalesAmount(final float salesAmount) {
        this.salesAmount = salesAmount;
    }

    public int getOrderType() {
        return orderType;
    }

    public void setOrderType(final int orderType) {
        this.orderType = orderType;
    }

    public float getDeductedAmount() {
        return deductedAmount;
    }

    public void setDeductedAmount(final float deductedAmount) {
        this.deductedAmount = deductedAmount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     * 获取场次id(即之前的dId)
     */
    public String getShowtimeId() {
        return showtimeId;
    }

    public void setShowtimeId(final String showtimeId) {
        this.showtimeId = showtimeId;
    }

    public String getSubOrderId() {
        return subOrderId;
    }

    public void setSubOrderId(final String subOrderId) {
        this.subOrderId = subOrderId;
    }

    public long getPayEndTime() {
        return payEndTime;
    }

    public void setPayEndTime(final long payEndTime) {
        this.payEndTime = payEndTime;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(final long createTime) {
        this.createTime = createTime;
    }

    public int getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(final int orderStatus) {
        this.orderStatus = orderStatus;
    }

    public boolean isReSelectSeat() {
        return isReSelectSeat;
    }

    public void setReSelectSeat(final boolean isReSelectSeat) {
        this.isReSelectSeat = isReSelectSeat;
    }

    public long getReSelectSeatEndTimeSecond() {
        return reSelectSeatEndTimeSecond;
    }

    public void setReSelectSeatEndTimeSecond(
            final long reSelectSeatEndTimeSecond) {
        this.reSelectSeatEndTimeSecond = reSelectSeatEndTimeSecond;
    }
}
