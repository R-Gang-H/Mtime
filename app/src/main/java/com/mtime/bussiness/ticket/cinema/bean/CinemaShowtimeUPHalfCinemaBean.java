package com.mtime.bussiness.ticket.cinema.bean;

import com.mtime.base.bean.MBaseBean;

import java.util.List;


public class CinemaShowtimeUPHalfCinemaBean extends MBaseBean {
    public static final int MTIME_SALES_FALG = 0;
    public static final int THREE_PARTY_SALES_FALG = 1;
    private int cinemaId;//影院Id  
    private String name;//影院名称
    private double longitude;//谷歌经度
    private double latitude;//谷歌维度
    private double baiduLongitude;//百度经度
    private double baiduLatitude;//百度维度
    private String address;//地址
    private String tel;//电话
    private int directSalesFlag; //是否是第三方直销订单  0：时光 1：第三方
    private long dsPlatformId; //第三方直销平台的ID
    private String dsPlatformLabel;// 第三方直销平台的标签

    public String getGovCinemaId() {
        return govCinemaId;
    }

    public void setGovCinemaId(String govCinemaId) {
        this.govCinemaId = govCinemaId;
    }

    private String govCinemaId;// 影院的专资编码，唯一。第三方平台也使用该编码。跳转时需要此ID。
    private CinemaShowtimeUPHalfFeatureBean feature;//特色设施
    private List<CinemaMoviesCouponActivityItem> activityList;
    private List<String> features;//影院特色数组[“IMAX厅",“自助取票”,“VIP厅”]

    public int getDirectSalesFlag() {
        return directSalesFlag;
    }

    public void setDirectSalesFlag(int directSalesFlag) {
        this.directSalesFlag = directSalesFlag;
    }

    public long getDsPlatformId() {
        return dsPlatformId;
    }

    public void setDsPlatformId(long dsPlatformId) {
        this.dsPlatformId = dsPlatformId;
    }

    public String getDsPlatformLabel() {
        return dsPlatformLabel;
    }

    public void setDsPlatformLabel(String dsPlatformLabel) {
        this.dsPlatformLabel = dsPlatformLabel;
    }

    public int getCinemaId() {
        return cinemaId;
    }

    public void setCinemaId(int cinemaId) {
        this.cinemaId = cinemaId;
    }

    public String getName() {
        if (name == null) {
            return "";
        } else {
            return name;
        }
    }

    public void setName(String name) {
        this.name = name;
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

    public String getAddress() {
        if (address == null) {
            return "";
        } else {
            return address;
        }
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTel() {
        if (tel == null) {
            return "";
        } else {
            return tel;
        }
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public List<CinemaMoviesCouponActivityItem> getActivityList() {
        return activityList;
    }

    public void setActivityList(List<CinemaMoviesCouponActivityItem> activityList) {
        this.activityList = activityList;
    }

    public List<String> getFeatures() {
        return features;
    }

    public void setFeatures(List<String> features) {
        this.features = features;
    }

    public CinemaShowtimeUPHalfFeatureBean getFeature() {
        return feature;
    }

    public void setFeature(CinemaShowtimeUPHalfFeatureBean feature) {
        this.feature = feature;
    }
}
