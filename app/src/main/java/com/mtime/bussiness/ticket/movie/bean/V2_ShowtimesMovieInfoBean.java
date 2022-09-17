package com.mtime.bussiness.ticket.movie.bean;


import com.mtime.base.bean.MBaseBean;

public class V2_ShowtimesMovieInfoBean extends MBaseBean {
	private int Id;
	private String title;

	public void setId(final int Id) {
		this.Id = Id;
	}

	public int getId() {
		return Id;
	}

	public void setTitle(final String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}

}
