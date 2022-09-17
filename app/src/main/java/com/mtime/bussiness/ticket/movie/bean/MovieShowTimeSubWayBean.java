package com.mtime.bussiness.ticket.movie.bean;

import com.mtime.base.bean.MBaseBean;

import java.util.List;

public class MovieShowTimeSubWayBean extends MBaseBean {
    private int sId;
    private List<Station> station;

    public int getSId() {
	return sId;
    }

    public void setSId(final int sId) {
	this.sId = sId;
    }

    public List<Station> getStation() {
	return station;
    }

    public void setStation(final List<Station> station) {
	this.station = station;
    }
}
