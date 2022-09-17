package com.mtime.bussiness.ticket.cinema.bean;

import com.mtime.base.bean.MBaseBean;

public class BranchCinemasBean extends MBaseBean {
    
    private int               cinemaId;
    private String            name;
    private String            address;
    private double            ratingFinal;
    private double            longitude;
    private double            latitude;
    private double            baiduLongitude;
    private double            baiduLatitude;
    private int               cityId;
    private CinemaViewFeature feature;
    
    public int getCinemaId() {
        return cinemaId;
    }
    
    public void setCinemaId(int cinemaId) {
        this.cinemaId = cinemaId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public double getRatingFinal() {
        return ratingFinal;
    }
    
    public void setRatingFinal(double ratingFinal) {
        this.ratingFinal = ratingFinal;
    }
    
    public double getLongitude() {
        return longitude;
    }
    
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
    
    public double getLatitude() {
        return latitude;
    }
    
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
    
    public double getBaiduLongitude() {
        return baiduLongitude;
    }
    
    public void setBaiduLongitude(double baiduLongitude) {
        this.baiduLongitude = baiduLongitude;
    }
    
    public double getBaiduLatitude() {
        return baiduLatitude;
    }
    
    public void setBaiduLatitude(double baiduLatitude) {
        this.baiduLatitude = baiduLatitude;
    }
    
    public int getCityId() {
        return cityId;
    }
    
    public void setCityId(int cityId) {
        this.cityId = cityId;
    }
    
    public CinemaViewFeature getFeature() {
        return feature;
    }
    
    public void setFeature(CinemaViewFeature feature) {
        this.feature = feature;
    }
    
}
