package com.mtime.bussiness.mine.bean;

import com.mtime.base.bean.MBaseBean;

/**
 * Created by zhulinping on 2017/6/13.
 */

public class MtimeCoinBean  extends MBaseBean {
    private int  couponId; // 优惠券id，即批次号
    private int couponType; // 优惠券类型，1:购物券，2:票务券
    private String title; // 优惠券名称
    private String image;// 图片地址
    private double amount; // 优惠券金额，单位:分
    private String rule; // 规则
    private long quota; // 兑换额度
    private int status; // 状态，1: 可兑换，2: 已兑完

    public int getCouponType() {
        return couponType;
    }

    public void setCouponType(int couponType) {
        this.couponType = couponType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getRule() {
        return rule;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }

    public long getQuota() {
        return quota;
    }

    public void setQuota(long quota) {
        this.quota = quota;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getCouponId() {

        return couponId;
    }

    public void setCouponId(int couponId) {
        this.couponId = couponId;
    }
}
