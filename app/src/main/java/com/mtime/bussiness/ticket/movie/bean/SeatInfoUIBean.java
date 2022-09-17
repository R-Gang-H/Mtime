package com.mtime.bussiness.ticket.movie.bean;

import com.mtime.base.bean.MBaseBean;
import com.mtime.bussiness.ticket.bean.Provider;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 选座信息
 */
public class SeatInfoUIBean extends MBaseBean implements Serializable {
    private static final long serialVersionUID = 6743269047326422315L;

    private String providerId; // 本场次的供应商id
    private boolean isSale; // 是否可售
    private double mtimeSellPrice; // 时光网销售价格,单位是分
    private String orderId; // 未支付的订单id，没有返回0
    private String subOrderID; // 子订单
    private String cinemaName; // 影院名称
    private String movieName; // 影片名称
    private String hallName; // 影厅名,
    private String versionDesc; // 版本,
    private String language; // 语言,
    private double salePrice; // 销售价,单位分
    private double serviceFee; // 服务费,单位分
    private long realTime; // 放映时间，格式：135123635241
    private List<Seat> allSeats;
    private String movieId;
    private String cinemaId;
    private Map<Integer, List<Seat>> ymap = new HashMap<Integer, List<Seat>>(); // 座位，key为y坐标，value为y坐标的对应的所有座位信息
    private List<Provider> providerList = new ArrayList<Provider>(); // 供应商
    private List<SalePriceBean>   salePriceList = new ArrayList<SalePriceBean>();//销售金额列表

    public boolean isSale() {
	return isSale;
    }

    public void setSale(final boolean isSale) {
	this.isSale = isSale;
    }

    public double getMtimeSellPrice() {
	return mtimeSellPrice;
    }

    public void setMtimeSellPrice(final double mtimeSellPrice) {
	this.mtimeSellPrice = mtimeSellPrice;
    }

    public List<Provider> getProviderList() {
	return providerList;
    }

    public void setProviderList(final List<Provider> providerList) {
	this.providerList = providerList;
    }

    public Map<Integer, List<Seat>> getYmap() {
	return ymap;
    }

    public void setYmap(final Map<Integer, List<Seat>> ymap) {
	this.ymap = ymap;
    }

    public String getProviderId() {
	return providerId;
    }

    public void setProviderId(final String providerId) {
	this.providerId = providerId;
    }

    public String getOrderId() {
	return orderId;
    }

    public void setOrderId(final String orderId) {
	this.orderId = orderId;
    }

    public String getSubOrderID() {
	return subOrderID;
    }

    public void setSubOrderID(final String subOrderID) {
	this.subOrderID = subOrderID;
    }

    public String getCinemaName() {
	return cinemaName;
    }

    public void setCinemaName(final String cinemaName) {
	this.cinemaName = cinemaName;
    }

    public String getMovieName() {
	return movieName;
    }

    public void setMovieName(final String movieName) {
	this.movieName = movieName;
    }

    public String getHallName() {
	return hallName;
    }

    public void setHallName(final String hallName) {
	this.hallName = hallName;
    }

    public String getVersionDesc() {
	return versionDesc;
    }

    public void setVersionDesc(final String versionDesc) {
	this.versionDesc = versionDesc;
    }

    public String getLanguage() {
	return language;
    }

    public void setLanguage(final String language) {
	this.language = language;
    }

    public double getSalePrice() {
	return salePrice;
    }

    public void setSalePrice(final double salePrice) {
	this.salePrice = salePrice;
    }

    public double getServiceFee() {
	return serviceFee;
    }

    public void setServiceFee(final double serviceFee) {
	this.serviceFee = serviceFee;
    }

    public long getRealTime() {
	return realTime;
    }

    public void setRealTime(final long realTime) {
	this.realTime = realTime;
    }

    /**
     * @return allSeats
     */
    public List<Seat> getAllSeats() {
	return allSeats;
    }

    /**
     * @param allSeats
     *            要设置的 allSeats
     */
    public void setAllSeats(final List<Seat> allSeats) {
	this.allSeats = allSeats;
    }

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }

    public String getCinemaId() {
        return cinemaId;
    }

    public void setCinemaId(String cinemaId) {
        this.cinemaId = cinemaId;
    }


    public List<SalePriceBean> getSalePriceList() {
        return salePriceList;
    }

    public void setSalePriceList(List<SalePriceBean> salePriceList) {
        this.salePriceList = salePriceList;
    }

}
