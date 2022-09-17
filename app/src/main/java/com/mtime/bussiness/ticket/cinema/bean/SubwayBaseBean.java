package com.mtime.bussiness.ticket.cinema.bean;

import com.mtime.base.bean.MBaseBean;

import java.util.List;

public class SubwayBaseBean extends MBaseBean {
    private int                   id;
    private String                name;
    private String                img;
    private int                   cinemaCount;
    private int                   stationCount;
    private String                lineMap;
    private List<StationBaseBean> stations;
    
    public String getLineMap() {
        return lineMap;
    }
    
    public void setLineMap(final String lineMap) {
        this.lineMap = lineMap;
    }
    
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
    
    public String getImg() {
        return img;
    }
    
    public void setImg(final String img) {
        this.img = img;
    }
    
    public int getCinemaCount() {
        return cinemaCount;
    }
    
    public void setCinemaCount(final int cinemaCount) {
        this.cinemaCount = cinemaCount;
    }
    
    public int getStationCount() {
        return stationCount;
    }
    
    public void setStationCount(final int stationCount) {
        this.stationCount = stationCount;
    }
    
    public List<StationBaseBean> getStations() {
        return stations;
    }
    
    public void setStations(final List<StationBaseBean> stations) {
        this.stations = stations;
    }
}
