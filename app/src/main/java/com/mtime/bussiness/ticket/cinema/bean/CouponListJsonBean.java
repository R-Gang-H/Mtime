package com.mtime.bussiness.ticket.cinema.bean;

import com.mtime.base.bean.MBaseBean;

import java.util.List;

/**
 * 影院概览页-优惠公告json实体bean
 * 
 * @author ye
 * 
 */
public class CouponListJsonBean extends MBaseBean {
    private List<Coupon> coupons;

    public List<Coupon> getCoupons() {
	return coupons;
    }

    public void setCoupons(final List<Coupon> coupons) {
	this.coupons = coupons;
    }
}
