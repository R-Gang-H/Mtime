package com.mtime.bussiness.ticket.movie.bean;

import com.mtime.base.bean.MBaseBean;

import java.util.List;

/**
 * 下订单时获取的json实体
 */
public class CreateOrderJsonBean extends MBaseBean {
    // 订单Id
    private long orderId;
    // 子订单Id
    private long subOrderId;
    // 下订单是否成功
    private boolean success;
    // 下订单提示的信息
    private String msg;
    // 下订单的状态
    private int status;
    // 支付结束时间（单位是秒）
    private long payEndTime;
    // 华为钱包--Pass类型标识（在开发者联盟注册的卡券类型Id）
    private String passTypeIdentifier; // "hwpass.com.mtime.membership.movie"
    // 是否添加小麦订单出错。true成功、反之失败
    private boolean isAddBuffetSubOrder;
    // 添加小卖失败Debug信息
    private String bufferError;
    // 添加小卖失败时出错的商品列表，只有传入的小卖商品ID字符串格式正确，且商品Id存在则返回该字段
    private List<FailCommodityIdBean> failedCommoditys;

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(final long orderId) {
        this.orderId = orderId;
    }

    public long getSubOrderId() {
        return subOrderId;
    }

    public void setSubOrderId(final long subOrderId) {
        this.subOrderId = subOrderId;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(final boolean success) {
        this.success = success;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(final int status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(final String msg) {
        this.msg = msg;
    }

    public long getPayEndTime() {
        return payEndTime;
    }

    public void setPayEndTime(final long payEndTime) {
        this.payEndTime = payEndTime;
    }

    public String getBufferError() {
        return bufferError;
    }

    public void setBufferError(String bufferError) {
        this.bufferError = bufferError;
    }

    public boolean isAddBuffetSubOrder() {
        return isAddBuffetSubOrder;
    }

    public void setAddBuffetSubOrder(boolean isAddBuffetSubOrder) {
        this.isAddBuffetSubOrder = isAddBuffetSubOrder;
    }

    public List<FailCommodityIdBean> getFailedCommoditys() {
        return failedCommoditys;
    }

    public void setFailedCommoditys(List<FailCommodityIdBean> failedCommoditys) {
        this.failedCommoditys = failedCommoditys;
    }

    public String getPassTypeIdentifier() {
        return passTypeIdentifier;
    }

    public void setPassTypeIdentifier(String passTypeIdentifier) {
        this.passTypeIdentifier = passTypeIdentifier;
    }
}
