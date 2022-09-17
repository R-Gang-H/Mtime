package com.mtime.bussiness.ticket.movie.bean;

import com.mtime.base.bean.MBaseBean;
import com.mtime.bussiness.ticket.bean.Provider;

import java.io.Serializable;
import java.util.List;

public class ShowTimeUIBean extends MBaseBean implements Serializable, Comparable<Object> {
    private static final long serialVersionUID = 1903016682615286693L;

    private int id;
    private long time; // 电影放映时间
    private String dateTime; // 放映日期
    private String price; // 票价--此字段需定义为String类型，不然服务端返回price数据为""时会崩溃
    private double salePrice; // 在线售票售价showtimeuibean    private double activityPrice;
    private double activityPrice;
    private String language; // 语言:"无","中文版","原版","粤语版","不详"
    private String version; // 版本:"无",
			    // "胶片",
			    // "数字",
			    // "Imax",
			    // "3D"
    private int duration; // 放映时长，可能为片长加广告长度。单位:分钟
    private String hall; // 影厅名
    private boolean isTicket; // 是否可以购票
    private List<Provider> providerList; // 供应商
    private String describe; // 描述
    private boolean valid; // 场次是否有效
    private String versionDesc; // 影片类型
    private double cinemaPrice; // 影院门市价格（单位是分，可以在线选座时有效）
    private int isSeatLess;//影厅剩余座位数状态 -1表示可售座位数无效、1表示少量、 2可售座位多、 0 其他
    private boolean isCoupon;
    private String seatSalesTip;

    public boolean isValid() {
	return valid;
    }

    public void setValid(final boolean valid) {
	this.valid = valid;
    }

    public int getId() {
	return id;
    }

    public void setId(final int id) {
	this.id = id;
    }

    public long getTime() {
	return time;
    }

    public void setTime(final long time) {
	this.time = time;
    }

    public String getPrice() {
	return price;
    }

    public void setPrice(final String price) {
	this.price = price;
    }

    public String getLanguage() {
	return language;
    }

    public void setLanguage(final String language) {
	this.language = language;
    }

    public String getVersion() {
	return version;
    }

    public void setVersion(final String version) {
	this.version = version;
    }

    public int getDuration() {
	return duration;
    }

    public void setDuration(final int duration) {
	this.duration = duration;
    }

    public String getHall() {
	return hall;
    }

    public void setHall(final String hall) {
	this.hall = hall;
    }

    public String getDateTime() {
	return dateTime;
    }

    public void setDateTime(final String dateTime) {
	this.dateTime = dateTime;
    }

    public boolean isTicket() {
	return isTicket;
    }

    public void setTicket(final boolean isTicket) {
	this.isTicket = isTicket;
    }

    public List<Provider> getProviderList() {
	return providerList;
    }

    public void setProviderList(final List<Provider> providerList) {
	this.providerList = providerList;
    }

    public String getDescribe() {
	return describe;
    }

    public void setDescribe(final String describe) {
	this.describe = describe;
    }

    public double getSalePrice() {
	return salePrice;
    }

    public void setSalePrice(final double salePrice) {
	this.salePrice = salePrice;
    }

    public String getVersionDesc() {
	return versionDesc;
    }

    public void setVersionDesc(final String versionDesc) {
	this.versionDesc = versionDesc;
    }

    public double getCinemaPrice() {
	return cinemaPrice;
    }

    public void setCinemaPrice(final double cinemaPrice) {
	this.cinemaPrice = cinemaPrice;
    }
    
    public int getIsSeatLess() {
        return isSeatLess;
    }

    public void setIsSeatLess(int isSeatLess) {
        this.isSeatLess = isSeatLess;
    }

    public double getActivityPrice() {
        return activityPrice;
    }

    public void setActivityPrice(double activityPrice) {
        this.activityPrice = activityPrice;
    }
    
    public boolean isCoupon() {
        return isCoupon;
    }

    public void setCoupon(boolean isCoupon) {
        this.isCoupon = isCoupon;
    }
    public String getSeatSalesTip() {
        if (seatSalesTip == null){
            return "";
        }
        return seatSalesTip;
    }

    public void setSeatSalesTip(String seatSalesTip) {
        this.seatSalesTip = seatSalesTip;
    }


    @Override
    public int compareTo(final Object another) {
	final ShowTimeUIBean s = (ShowTimeUIBean) another;
	return (int) (getTime() - s.getTime());
    }
}
