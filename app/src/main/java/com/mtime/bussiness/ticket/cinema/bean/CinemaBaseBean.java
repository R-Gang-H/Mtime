package com.mtime.bussiness.ticket.cinema.bean;

import com.mtime.base.bean.MBaseBean;

public class CinemaBaseBean extends MBaseBean {
    private int     id;
    private String  name;
    private String  address;
    private String  tele;
    private double  latitude;
    private double  longitude;
    private int     districtId;
    private int     cityId;
    private int     provinceId;
    private String  postCode;
    private String  route;
    private String  showLeft;
    private String  showOften;
    private int     isTicket;
    private int     isEticket;
    private double  minPrice;
    private String  distance;
    private boolean isoffen = false; // 常去标识
                                     
    public String getShowOften() {
        return showOften;
    }
    
    public void setShowOften(final String showOften) {
        this.showOften = showOften;
    }
    
    public String getShowLeft() {
        return showLeft;
    }
    
    public void setShowLeft(final String showLeft) {
        this.showLeft = showLeft;
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
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(final String address) {
        this.address = address;
    }
    
    public String getTele() {
        return tele;
    }
    
    public void setTele(final String tele) {
        this.tele = tele;
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
    
    public double getDistrictId() {
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
    
    public int getProvinceId() {
        return provinceId;
    }
    
    public void setProvinceId(final int provinceId) {
        this.provinceId = provinceId;
    }
    
    public String getPostCode() {
        return postCode;
    }
    
    public void setPostCode(final String postCode) {
        this.postCode = postCode;
    }
    
    public String getRoute() {
        return route;
    }
    
    public void setRoute(final String route) {
        this.route = route;
    }
    
    public int getIsEticket() {
        return isEticket;
    }
    
    public void setIsEticket(final int isEticket) {
        this.isEticket = isEticket;
    }
    
    public int getIsTicket() {
        return isTicket;
    }
    
    public void setIsTicket(final int isTicket) {
        this.isTicket = isTicket;
    }
    
    public double getMinPrice() {
        return minPrice;
    }
    
    public void setMinPrice(final double minPrice) {
        this.minPrice = minPrice;
    }
    
    /**
     *
     */
    public boolean isIsoffen() {
        return isoffen;
    }
    
    /**
     *
     */
    public void setIsoffen(final boolean isoffen) {
        this.isoffen = isoffen;
    }
    
    public String getDistance() {
        return distance;
    }
    
    public void setDistance(String distance) {
        this.distance = distance;
    }
}
