package com.mtime.bussiness.ticket.bean;

import com.mtime.base.bean.MBaseBean;
import com.mtime.bussiness.ticket.cinema.bean.SubwayBaseBean;
import com.mtime.bussiness.ticket.cinema.bean.BusinessAreaBean;
import com.mtime.bussiness.ticket.cinema.bean.BusinessBaseCinema;
import com.mtime.bussiness.ticket.cinema.bean.CinemaBaseBean;
import com.mtime.bussiness.ticket.cinema.bean.SubwayCinemaBaseBean;

import java.util.List;

public class BaseCityBean extends MBaseBean {
    private List<CinemaBaseBean> cinemas;
    private List<BusinessBaseCinema> businessCinemas;
    private List<BusinessAreaBean> businessAreas;
    private List<DistrictBaseBean> districts;
    private List<SubwayBaseBean> subways;
    private List<SubwayCinemaBaseBean> subwayCinemas;

    public List<CinemaBaseBean> getCinemas() {
	return cinemas;
    }

    public void setCinemas(final List<CinemaBaseBean> cinemas) {
	this.cinemas = cinemas;
    }

    public List<BusinessBaseCinema> getBusinessCinemas() {
	return businessCinemas;
    }

    public void setBusinessCinemas(
	    final List<BusinessBaseCinema> businessCinemas) {
	this.businessCinemas = businessCinemas;
    }

    public List<DistrictBaseBean> getDistricts() {
	return districts;
    }

    public void setDistricts(final List<DistrictBaseBean> districts) {
	this.districts = districts;
    }

    public List<SubwayBaseBean> getSubways() {
	return subways;
    }

    public void setSubways(final List<SubwayBaseBean> subways) {
	this.subways = subways;
    }

    public List<SubwayCinemaBaseBean> getSubwayCinemas() {
	return subwayCinemas;
    }

    public void setSubwayCinemas(final List<SubwayCinemaBaseBean> subwayCinemas) {
	this.subwayCinemas = subwayCinemas;
    }

    public List<BusinessAreaBean> getBusinessAreas() {
	return businessAreas;
    }

    public void setBusinessAreas(final List<BusinessAreaBean> businessAreas) {
	this.businessAreas = businessAreas;
    }
}
