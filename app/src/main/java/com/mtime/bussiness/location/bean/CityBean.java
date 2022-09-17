package com.mtime.bussiness.location.bean;

import com.mtime.base.bean.MBaseBean;

public class CityBean extends MBaseBean {

    // 城市编号
    private int id;
    // 城市名称
    private String n;
    private String pinyinFull;
    private String pinyinShort;

    public CityBean() {
	super();
    }

    public String getPinyinShort() {
	return pinyinShort;
    }

    public void setPinyinShort(final String pinyinShort) {
	this.pinyinShort = pinyinShort;
    }

    public CityBean(final int id, final String n) {
	super();
	this.id = id;
	this.n = n;
    }

    public int getId() {
	return id;
    }

    public void setId(final int id) {
	this.id = id;
    }

    public String getName() {
	return n;
    }

    public void setN(final String n) {
	this.n = n;
    }

    /**
     *
     */
    public String getPinyinFull() {
	return pinyinFull;
    }

    /**
     *
     */
    public void setPinyinFull(final String pinyinFull) {
	this.pinyinFull = pinyinFull;
    }
}
