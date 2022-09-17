package com.mtime.bussiness.ticket.cinema.bean;

import com.mtime.base.bean.MBaseBean;

public class StationBaseBean extends MBaseBean {
    private int stId;
    private String stName;
    private int cinemaCount;
    private double latitude;
    private double longitude;
    private int orderNum;

    public int getStId() {
	return stId;
    }

    public void setStId(final int stId) {
	this.stId = stId;
    }

    public String getStName() {
	return stName;
    }

    public void setStName(final String stName) {
	this.stName = stName;
    }

    public int getCinemaCount() {
	return cinemaCount;
    }

    public void setCinemaCount(final int cinemaCount) {
	this.cinemaCount = cinemaCount;
    }

    public double getLatitude() {
	return latitude;
    }

    public void setLatitude(final double latitude) {
	this.latitude = latitude;
    }

    public double getLongitude() {
	return longitude;
    }

    public void setLongitude(final double longitude) {
	this.longitude = longitude;
    }

    public int getOrderNum() {
	return orderNum;
    }

    public void setOrderNum(final int orderNum) {
	this.orderNum = orderNum;
    }
}
