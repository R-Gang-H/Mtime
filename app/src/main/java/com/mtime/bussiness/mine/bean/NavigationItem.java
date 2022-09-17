package com.mtime.bussiness.mine.bean;

import com.mtime.base.bean.MBaseBean;

public class NavigationItem extends MBaseBean {

	private int type;
	private String title;

	public NavigationItem(int type, String title) {
		this.type = type;
		this.title = title;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}


}
