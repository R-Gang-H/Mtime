package com.mtime.bussiness.ticket.movie.bean;

import com.mtime.base.bean.MBaseBean;
import com.mtime.bussiness.ticket.cinema.bean.CinemaShowtimeBean;
import com.mtime.bussiness.ticket.cinema.bean.CouponActivityItem;
import com.mtime.bussiness.ticket.cinema.bean.ShowtimeJsonBean;

import java.util.ArrayList;
import java.util.List;

public class MovieShowTimeCinemaBean extends MBaseBean {
    private int cid;
    private String cn;
    private String ln;
    private int sC;
    private String address;
    private double latitude;
    private double longitude;
    private double baiduLatitude;
    private double baiduLongitude;
    private int districtId;
    private int cityId;
    private List<MovieShowTimeBusinessBean> business;
    private String rating;
    private boolean isTicket;
    private double minPrice;
    private boolean isVaildTicket;
    private boolean isCurrentDayIMAX;
    private long endTime;
    private long startTime;
    private List<String> recentDates;
    private List<MovieShowTimeSubWayBean> subway;
    private List<ShowtimeJsonBean> movieTimeChildBeans = new ArrayList<ShowtimeJsonBean>();
    private String showLeft;
    private V2_ShowTimeCinemaFeature feature;
    private boolean favorited;
    private List<CouponActivityItem> couponActivityList;
    private int position;
    private List<CinemaShowtimeBean> showtimeList = new ArrayList<>();
    private int directSalesFlag;        // 是否是第三方直销影院  0：不是 1：是第三方
    private long dsPlatformId;          // 第三方直销平台的ID
    private String dsPlatformLabel;     // 第三方直销平台的标签
    private String govCinemaId;         // 影院专资码

    public void setPosition(int position){
        this.position=position;
    }
    public int getPosition(){
        return position;
    }

    public boolean getFavorited() {
        return favorited;
    }

    public void setFavorited(final boolean favorited) {
        this.favorited = favorited;
    }

    public V2_ShowTimeCinemaFeature getFeature() {
        return feature;
    }

    public void setFeature(final V2_ShowTimeCinemaFeature feature) {
        this.feature = feature;
    }

    public String getShowLeft() {
        return showLeft;
    }

    public void setShowLeft(final String showLeft) {
        this.showLeft = showLeft;
    }

    public List<String> getRecentDates() {
        return recentDates;
    }

    public void setRecentDates(final List<String> recentDates) {
        this.recentDates = recentDates;
    }

    public List<ShowtimeJsonBean> getMovieTimeChildBeans() {
        return movieTimeChildBeans;
    }

    public void setMovieTimeChildBeans(
            final List<ShowtimeJsonBean> movieTimeChildBeans) {
        this.movieTimeChildBeans = movieTimeChildBeans;
    }

    public int getCid() {
        return cid;
    }

    public void setCid(final int cid) {
        this.cid = cid;
    }

    public String getCn() {
        return cn;
    }

    public void setCn(final String cn) {
        this.cn = cn;
    }

    public String getLn() {
        return ln;
    }

    public void setLn(final String ln) {
        this.ln = ln;
    }

    public int getSC() {
        return sC;
    }

    public void setSC(final int sC) {
        this.sC = sC;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(final String address) {
        this.address = address;
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

    public int getDistrictId() {
        return districtId;
    }

    public void setDistrictId(final int districtId) {
        this.districtId = districtId;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(final int cityId) {
        this.cityId = cityId;
    }

    public List<MovieShowTimeBusinessBean> getBusiness() {
        return business;
    }

    public void setBusiness(final List<MovieShowTimeBusinessBean> business) {
        this.business = business;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(final String rating) {
        this.rating = rating;
    }

    public boolean getIsTicket() {
        return isTicket;
    }

    public void setIsTicket(final boolean isTicket) {
        this.isTicket = isTicket;
    }

    public double getMinPrice() {
        return minPrice / 100;
    }

    public double getMinPriceFen() {
        return minPrice;
    }

    public void setMinPrice(final double minPrice) {
        this.minPrice = minPrice;
    }

    public List<MovieShowTimeSubWayBean> getSubway() {
        return subway;
    }

    public void setSubway(final List<MovieShowTimeSubWayBean> subway) {
        this.subway = subway;
    }

    public boolean isVaildTicket() {
        return isVaildTicket;
    }

    public void setVaildTicket(final boolean isVaildTicket) {
        this.isVaildTicket = isVaildTicket;
    }

    public boolean getIsCurrentDayIMAX() {
        return isCurrentDayIMAX;
    }

    public void setIsCurrentDayIMAX(final boolean isCurrentDayIMAX) {
        this.isCurrentDayIMAX = isCurrentDayIMAX;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(final long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(final long endTime) {
        this.endTime = endTime;
    }

    public double getBaiduLatitude() {
        return baiduLatitude;
    }

    public void setBaiduLatitude(double baiduLatitude) {
        this.baiduLatitude = baiduLatitude;
    }

    public double getBaiduLongitude() {
        return baiduLongitude;
    }

    public void setBaiduLongitude(double baiduLongitude) {
        this.baiduLongitude = baiduLongitude;
    }

    public void setCouponActivityList(final List<CouponActivityItem> coupons) {
        this.couponActivityList = coupons;
    }

    public List<CouponActivityItem> getCouponActivityList() {
        return this.couponActivityList;
    }

    public List<CinemaShowtimeBean> getShowtimeList() {
        return showtimeList;
    }

    public void setShowtimeList(List<CinemaShowtimeBean> showtimeList) {
        this.showtimeList = showtimeList;
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
