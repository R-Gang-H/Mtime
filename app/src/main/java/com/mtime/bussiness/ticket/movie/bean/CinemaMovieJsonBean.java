package com.mtime.bussiness.ticket.movie.bean;

import com.mtime.base.bean.MBaseBean;

import java.util.List;

public class CinemaMovieJsonBean extends MBaseBean {
    private int lid;
    private String bImg;
    private String date;
    private int totalComingMovie;
    private long newActivitiesTime;
    private String voucherMsg;
    private List<MovieBean> ms;
    private boolean hasPromo;
    private MovieAdBean promo;

    public String getbImg() {
        return bImg;
    }

    public void setbImg(String bImg) {
        this.bImg = bImg;
    }

    public boolean isHasPromo() {
        return hasPromo;
    }

    public void setHasPromo(boolean hasPromo) {
        this.hasPromo = hasPromo;
    }

    public MovieAdBean getPromo() {
        return promo;
    }

    public void setPromo(MovieAdBean promo) {
        this.promo = promo;
    }

    public int getLid() {
	return lid;
    }

    public void setLid(final int lid) {
	this.lid = lid;
    }

    public String getBImg() {
	return bImg;
    }

    public void setBImg(final String bImg) {
	this.bImg = bImg;
    }

    public String getDate() {
	return date;
    }

    public void setDate(final String date) {
	this.date = date;
    }
    
    public long getNewActivitiesTime() {
        return newActivitiesTime;
    }

    public void setNewActivitiesTime(long newActivitiesTime) {
        this.newActivitiesTime = newActivitiesTime;
    }

    public List<MovieBean> getMs() {
	return ms;
    }

    public void setMs(final List<MovieBean> ms) {
	this.ms = ms;
    }

    public int getTotalComingMovie() {
        return totalComingMovie;
    }

    public void setTotalComingMovie(int totalComingMovie) {
        this.totalComingMovie = totalComingMovie;
    }
    public String getVoucherMsg() {
        return voucherMsg;
    }

    public void setVoucherMsg(final String voucherMsg) {
        this.voucherMsg = voucherMsg;
    }
}
