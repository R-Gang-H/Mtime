package com.mtime.bussiness.ticket.cinema.bean;

import com.mtime.base.bean.MBaseBean;
import com.mtime.bussiness.ticket.bean.Provider;

import java.util.List;

/**
 * Created by vivian.wei on 2017/10/16.
 * 影院场次
 */

public class CinemaShowtimeBean extends MBaseBean {

    private String capacity;
    private String hall;            // 5号厅
    private String effect;
    private String language;        // 中文版
    private String seatSalesTip;
    private String versionDesc;     // 2D
    private String price;
    private int hallID;
    private int cinemaPrice;
    private int salePrice;
    private int length;
    private int seatTotal;
    private int version;
    private int isSeatLess;
    private long showDay;
    private long sId;
    private long endTime;
    private long startTime;
    private boolean isCoupon;
    private boolean isMovies;
    private boolean isTicket;
    private boolean isVaildTicket;
    private List<Provider> providers;

    public String getCapacity() {
        return capacity;
    }

    public void setCapacity(String capacity) {
        this.capacity = capacity;
    }

    public String getHall() {
        return hall;
    }

    public void setHall(String hall) {
        this.hall = hall;
    }

    public String getEffect() {
        return effect;
    }

    public void setEffect(String effect) {
        this.effect = effect;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getSeatSalesTip() {
        return seatSalesTip;
    }

    public void setSeatSalesTip(String seatSalesTip) {
        this.seatSalesTip = seatSalesTip;
    }

    public String getVersionDesc() {
        return versionDesc;
    }

    public void setVersionDesc(String versionDesc) {
        this.versionDesc = versionDesc;
    }

    public int getHallID() {
        return hallID;
    }

    public void setHallID(int hallID) {
        this.hallID = hallID;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getCinemaPrice() {
        return cinemaPrice;
    }

    public void setCinemaPrice(int cinemaPrice) {
        this.cinemaPrice = cinemaPrice;
    }

    public int getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(int salePrice) {
        this.salePrice = salePrice;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getSeatTotal() {
        return seatTotal;
    }

    public void setSeatTotal(int seatTotal) {
        this.seatTotal = seatTotal;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getIsSeatLess() {
        return isSeatLess;
    }

    public void setIsSeatLess(int isSeatLess) {
        this.isSeatLess = isSeatLess;
    }

    public long getShowDay() {
        return showDay;
    }

    public void setShowDay(long showDay) {
        this.showDay = showDay;
    }

    public long getsId() {
        return sId;
    }

    public void setsId(long sId) {
        this.sId = sId;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public boolean isCoupon() {
        return isCoupon;
    }

    public void setCoupon(boolean coupon) {
        isCoupon = coupon;
    }

    public boolean isMovies() {
        return isMovies;
    }

    public void setMovies(boolean movies) {
        isMovies = movies;
    }

    public boolean isTicket() {
        return isTicket;
    }

    public void setTicket(boolean ticket) {
        isTicket = ticket;
    }

    public boolean isVaildTicket() {
        return isVaildTicket;
    }

    public void setVaildTicket(boolean vaildTicket) {
        isVaildTicket = vaildTicket;
    }

    public List<Provider> getProviders() {
        return providers;
    }

    public void setProviders(List<Provider> providers) {
        this.providers = providers;
    }
}
