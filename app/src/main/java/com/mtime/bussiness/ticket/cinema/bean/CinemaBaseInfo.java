package com.mtime.bussiness.ticket.cinema.bean;

import com.mtime.base.bean.MBaseBean;

import java.util.ArrayList;
import java.util.List;

public class CinemaBaseInfo extends MBaseBean {

    private String address;
    private int     cinemaId;
    private String  cinameName;
    private int     districtID;
    private boolean isETicket;
    private boolean isTicket;
    private double  ratingFinal;
    private int     movieCount;
    private int     showtimeCount;
    private int     minPrice;
    private double  longitude;
    private double  latitude;
    private double  baiduLongitude;
    private double  baiduLatitude;
    private CinemaResultFeatureBean feature;
    private List<CouponActivityItem> couponActivityList;                //优惠活动列表
    private List<CinemaShowtimeBean> showtimeList = new ArrayList<>();  //影讯列表
    private int directSalesFlag;                                        //是否是第三方直销影院  0：不是 1：是第三方
    private long dsPlatformId;                                          //第三方直销平台的ID
    private String dsPlatformLabel;                                     // 第三方直销平台的标签
    private String govCinemaId;                                         // 影院专资码

    // 以下为非接口直接返回字段，用于排序和显示
    private double distance;
    private boolean isFavorit;  // 收藏
    private boolean isBeen;     // 去过
    private String movieId;     // 影片Id, 影片排片页使用

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<CouponActivityItem> getCouponActivityList() {
        return this.couponActivityList;
    }

    public void setCouponActivityList(final List<CouponActivityItem> couponActivityList) {
        this.couponActivityList = couponActivityList;
    }
    
    public int getCinemaId() {
        return cinemaId;
    }
    
    public void setCinemaId(final int cinemaId) {
        this.cinemaId = cinemaId;
    }
    
    public String getCinameName() {
        return cinameName;
    }
    
    public void setCinameName(final String cinameName) {
        this.cinameName = cinameName;
    }
    
    public int getDistrictID() {
        return districtID;
    }
    
    public void setDistrictID(final int districtID) {
        this.districtID = districtID;
    }
    
    public boolean isETicket() {
        return isETicket;
    }
    
    public void setETicket(final boolean isETicket) {
        this.isETicket = isETicket;
    }
    
    public boolean isTicket() {
       return this.isTicket;
    }
    
    public void setTicket(final boolean isTicket) {
        this.isTicket = isTicket;
    }
    
    public int getMovieCount() {
        return movieCount;
    }
    
    public void setMovieCount(final int movieCount) {
        this.movieCount = movieCount;
    }
    
    public int getShowtimeCount() {
        return showtimeCount;
    }
    
    public void setShowtimeCount(final int showtimeCount) {
        this.showtimeCount = showtimeCount;
    }
    
    public double getRatingFinal() {
        return ratingFinal;
    }
    
    public void setRatingFinal(final double ratingFinal) {
        this.ratingFinal = ratingFinal;
    }

    public int getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(int minPrice) {
        this.minPrice = minPrice;
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

    public CinemaResultFeatureBean getFeature() {
        return feature;
    }

    public void setFeature(CinemaResultFeatureBean feature) {
        this.feature = feature;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public boolean isFavorit() {
        return isFavorit;
    }

    public void setFavorit(boolean favorit) {
        isFavorit = favorit;
    }

    public boolean isBeen() {
        return isBeen;
    }

    public void setBeen(boolean been) {
        isBeen = been;
    }

    public List<CinemaShowtimeBean> getShowtimeList() {
        return showtimeList;
    }

    public void setShowtimeList(List<CinemaShowtimeBean> showtimeList) {
        this.showtimeList = showtimeList;
    }

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }

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

    public String getGovCinemaId() {
        return govCinemaId;
    }

    public void setGovCinemaId(String govCinemaId) {
        this.govCinemaId = govCinemaId;
    }
}
