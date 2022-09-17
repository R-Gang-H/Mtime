package com.mtime.bussiness.ticket.cinema.bean;

import com.mtime.base.bean.MBaseBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vivian.wei on 2017/10/17.
 * 影院列表页面筛选_地铁站
 */

public class StationBean extends MBaseBean {

    private int stId;                                       // 站点Id
    private String stName;                                  // 站点名称
    private int cinemaCount;                                // 附近影院数
    private List<Integer> cinemaIds = new ArrayList<>();    // 影院Id数组
    private double latitude;                                // 纬度
    private double longitude;                               // 经度
    private int orderNum;                                   // 站点序号
    private List<StationCinemaBean> cinemas = new ArrayList<>(); // 影院列表

    public int getStId() {
        return stId;
    }

    public void setStId(int stId) {
        this.stId = stId;
    }

    public String getStName() {
        return stName;
    }

    public void setStName(String stName) {
        this.stName = stName;
    }

    public int getCinemaCount() {
        return cinemaCount;
    }

    public void setCinemaCount(int cinemaCount) {
        this.cinemaCount = cinemaCount;
    }

    public List<Integer> getCinemaIds() {
        return cinemaIds;
    }

    public void setCinemaIds(List<Integer> cinemaIds) {
        this.cinemaIds = cinemaIds;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(int orderNum) {
        this.orderNum = orderNum;
    }

    public List<StationCinemaBean> getCinemas() {
        return cinemas;
    }

    public void setCinemas(List<StationCinemaBean> cinemas) {
        this.cinemas = cinemas;
    }
}
