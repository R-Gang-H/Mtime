package com.mtime.bussiness.ticket.cinema.bean;

import com.mtime.base.bean.MBaseBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vivian.wei on 2017/10/17.
 * 影院列表页面筛选_地铁线
 */

public class SubwayBean extends MBaseBean {

    private int subwayId;                                   // 地铁Id
    private String name;                                    // 地铁线名称
    private String img;                                     // 地铁图
    private int cinemaCount;                                // 附近影院数
    private List<Integer> cinemaIds = new ArrayList<>();    // 影院Id数组
    private int stationCount;                               // 站点数
    private List<StationBean> stations = new ArrayList<>(); // 车站列表

    public int getSubwayId() {
        return subwayId;
    }

    public void setSubwayId(int subwayId) {
        this.subwayId = subwayId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
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

    public int getStationCount() {
        return stationCount;
    }

    public void setStationCount(int stationCount) {
        this.stationCount = stationCount;
    }

    public List<StationBean> getStations() {
        return stations;
    }

    public void setStations(List<StationBean> stations) {
        this.stations = stations;
    }
}
