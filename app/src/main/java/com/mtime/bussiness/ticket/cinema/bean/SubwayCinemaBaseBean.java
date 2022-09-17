package com.mtime.bussiness.ticket.cinema.bean;

import com.mtime.base.bean.MBaseBean;

public class SubwayCinemaBaseBean extends MBaseBean {
    private int cId;
    private int subwayId;
    private int stationId;
    private String distance;
    public int getCId() {
	return cId;
    }

    public void setCId(final int cId) {
	this.cId = cId;
    }

    public int getSubwayId() {
	return subwayId;
    }

    public void setSubwayId(final int subwayId) {
	this.subwayId = subwayId;
    }

    public int getStationId() {
	return stationId;
    }

    public void setSationId(final int stationId) {
	this.stationId = stationId;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }
}
