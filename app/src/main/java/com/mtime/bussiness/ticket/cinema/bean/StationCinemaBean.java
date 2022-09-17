package com.mtime.bussiness.ticket.cinema.bean;

import com.mtime.base.bean.MBaseBean;

/**
 * Created by vivian.wei on 2017/10/17.
 * 影院列表页面筛选_地铁站_影院
 */

public class StationCinemaBean extends MBaseBean {
    private int cinemaId;     // 影院id
    private double distance;  // 影院到地铁站距离，单位米

    public int getCinemaId() {
        return cinemaId;
    }

    public void setCinemaId(int cinemaId) {
        this.cinemaId = cinemaId;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}
