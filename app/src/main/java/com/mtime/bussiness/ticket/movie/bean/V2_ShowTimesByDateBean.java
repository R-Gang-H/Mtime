package com.mtime.bussiness.ticket.movie.bean;

import com.mtime.base.bean.MBaseBean;

import java.util.List;

public class V2_ShowTimesByDateBean  extends MBaseBean {
    private int sid;
    private long showDay;
    private boolean isMovies;
    private boolean isCoupon;
    private int version;
    private String versionDesc;
    private String language;
    private int length;
    private String price;
    private double cinemaPrice;
    private double salePrice;
    private String hall;
    private int hallID;
    private long endTime;
    private long startTime;
    private boolean isVaildTicket;
    private boolean isTicket;
    private int isSeatLess;
    private String seatSalesTip;

    private List<ProviderBean> provider;

    public int getIsSeatLess() {
        return isSeatLess;
    }

    public void setIsSeatLess(final int isSeatLess) {
        this.isSeatLess = isSeatLess;
    }

    public String getSeatSalesTip() {
        if (seatSalesTip == null) {
            return "";
        }
        return seatSalesTip;
    }

    public void setSeatSalesTip(String seatSalesTip) {
        this.seatSalesTip = seatSalesTip;
    }

    public boolean getIsVaildTicket() {
        return isVaildTicket;
    }

    public void setIsVaildTicket(final boolean isVaildTicket) {
        this.isVaildTicket = isVaildTicket;
    }

    public boolean getIsCoupon() {
        return isCoupon;
    }

    public void setIsCoupon(final boolean isCoupon) {
        this.isCoupon = isCoupon;
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

    public boolean getIsMovies() {
        return isMovies;
    }

    public void setIsMovies(final boolean isMovies) {
        this.isMovies = isMovies;
    }

    public int getSid() {
        return sid;
    }

    public void setSid(final int sid) {
        this.sid = sid;
    }

    public long getShowDay() {
        return showDay;
    }

    public void setShowDay(final long showDay) {
        this.showDay = showDay;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(final int version) {
        this.version = version;
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

    public int getLength() {
        return length;
    }

    public void setLength(final int length) {
        this.length = length;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(final String price) {
        this.price = price;
    }

    public double getCinemaPrice() {
        return cinemaPrice / 100;
    }

    public void setCinemaPrice(final double cinemaPrice) {
        this.cinemaPrice = cinemaPrice;
    }

    public double getSalePrice() {
        return salePrice / 100;
    }

    public void setSalePrice(final double salePrice) {
        this.salePrice = salePrice;
    }

    public String getHall() {
        return hall;
    }

    public void setHall(final String hall) {
        this.hall = hall;
    }

    public int getHallID() {
        return hallID;
    }

    public void setHallID(final int hallID) {
        this.hallID = hallID;
    }

    public boolean getIsTicket() {
        return isTicket;
    }

    public void setIsTicket(final boolean isTicket) {
        this.isTicket = isTicket;
    }

    public List<ProviderBean> getProvider() {
        return provider;
    }

    public void setProvider(final List<ProviderBean> provider) {
        this.provider = provider;
    }
}
