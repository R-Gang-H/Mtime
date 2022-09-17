package com.mtime.bussiness.ticket.movie.bean;

import com.mtime.base.bean.MBaseBean;
import com.mtime.bussiness.ticket.cinema.bean.ShowtimeJsonBean;

import java.util.List;

public class CinemaJsonBean  extends MBaseBean {
    private String cn;
    private double rating;
    private String address;
    private boolean isETicket;
    private List<ShowtimeJsonBean> s;

    public String getName() {
	return cn;
    }

    public void setCn(final String cn) {
	this.cn = cn;
    }

    public double getRating() {
	return rating;
    }

    public void setRating(final double rating) {
	this.rating = rating;
    }

    public String getAddress() {
	return address;
    }

    public void setAddress(final String address) {
	this.address = address;
    }

    public boolean isETicket() {
	return isETicket;
    }

    public void setETicket(final boolean isETicket) {
	this.isETicket = isETicket;
    }

    public List<ShowtimeJsonBean> getS() {
	return s;
    }

    public void setS(final List<ShowtimeJsonBean> s) {
	this.s = s;
    }
}
