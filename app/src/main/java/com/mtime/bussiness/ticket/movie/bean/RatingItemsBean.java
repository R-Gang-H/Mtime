package com.mtime.bussiness.ticket.movie.bean;

import com.mtime.base.bean.MBaseBean;

public class RatingItemsBean extends MBaseBean {

	private int rDirector;
	private int rOther;
	private int rPicture;
	private int rShow;
	private int rStory;
	private int rTotal;

	public int getrDirector() {
		return rDirector;
	}

	public void setrDirector(final int rDirector) {
		this.rDirector = rDirector;
	}
	public int getrOther() {
		return rOther;
	}

	public void setrOther(final int rOther) {
		this.rOther = rOther;
	}
	public int getrPicture() {
		return rPicture;
	}

	public void setrPicture(final int rPicture) {
		this.rPicture = rPicture;
	}
	public int getrShow() {
		return rShow;
	}

	public void setrShow(final int rShow) {
		this.rShow = rShow;
	}

	public void setrStory(final int rStory) {
		this.rStory= rStory;
	}
	public int getrStory() {
		return rStory;
	}

	public void setrTotal(final int rTotal) {
		this.rTotal = rTotal;
	}
	public int getrTotal() {
		return rTotal;
	}
}
