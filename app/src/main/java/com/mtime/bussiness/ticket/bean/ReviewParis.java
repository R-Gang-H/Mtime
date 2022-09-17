package com.mtime.bussiness.ticket.bean;


import com.mtime.base.bean.MBaseBean;

public class ReviewParis extends MBaseBean {
	private int reviewId;
	private int totalPraise;
	private boolean isPraise;

	public int getReviewId() {
		return reviewId;
	}

	public void setReviewId(int reviewId) {
		this.reviewId = reviewId;
	}

	public int getTotalPraise() {
		return totalPraise;
	}

	public void setTotalPraise(int totalPraise) {
		this.totalPraise = totalPraise;
	}

	public boolean getIsPraise() {
		return isPraise;
	}

	public void setIsPraise(boolean isPraise) {
		this.isPraise = isPraise;
	}
}
