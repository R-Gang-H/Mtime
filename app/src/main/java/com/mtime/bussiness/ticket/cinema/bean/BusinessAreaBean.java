package com.mtime.bussiness.ticket.cinema.bean;

import com.mtime.base.bean.MBaseBean;

public class BusinessAreaBean extends MBaseBean {
    private int id;
    private String name;
    private int provinceID;
    private int cityId;
    private int districtId;

    public int getId() {
	return id;
    }

    public void setId(final int id) {
	this.id = id;
    }

    public String getName() {
	return name;
    }

    public void setName(final String name) {
	this.name = name;
    }

    public int getProvinceID() {
	return provinceID;
    }

    public void setProvinceID(final int provinceID) {
	this.provinceID = provinceID;
    }

    public int getCityId() {
	return cityId;
    }

    public void setCityId(final int cityId) {
	this.cityId = cityId;
    }

    public int getDistrictId() {
	return districtId;
    }

    public void setDistrictId(final int districtId) {
	this.districtId = districtId;
    }
}
