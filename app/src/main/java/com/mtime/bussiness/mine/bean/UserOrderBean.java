package com.mtime.bussiness.mine.bean;

import com.mtime.base.bean.MBaseBean;

import java.util.List;

public class UserOrderBean  extends MBaseBean {
    private int totalCount;
    private boolean isMore;
    private List<OrderBean> orders;

    public int getTotalCount() {
	return totalCount;
    }

    public void setTotalCount(final int totalCount) {
	this.totalCount = totalCount;
    }

    public List<OrderBean> getOrders() {
	return orders;
    }

    public void setOrders(final List<OrderBean> orders) {
	this.orders = orders;
    }

	public boolean isMore() {
		return isMore;
	}

	public void setMore(boolean isMore) {
		this.isMore = isMore;
	}
}
