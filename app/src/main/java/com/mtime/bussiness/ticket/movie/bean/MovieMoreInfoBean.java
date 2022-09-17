package com.mtime.bussiness.ticket.movie.bean;

import com.mtime.base.bean.MBaseBean;
import com.mtime.bussiness.ticket.movie.activity.MovieMoreInfoActivity;

import java.util.List;

/**
 * 用于{@link MovieMoreInfoActivity}
 */
public class MovieMoreInfoBean extends MBaseBean {
    private List<String> name;
    private String length;
    private String cost;
    private String language;
    private List<MoreInfoWebBean> officialWebsite;
    private String captureDate;

    public List<String> getName() {
        return name;
    }

    public String getLength() {
        return length;
    }

    public String getCost() {
        return cost;
    }

    public String getLanguage() {
        return language;
    }

    public List<MoreInfoWebBean> getOfficialWebsite() {
        return officialWebsite;
    }

    public String getCaptureDate() {
        return captureDate;
    }

    public void setName(List<String> name) {
        this.name = name;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public void setOfficialWebsite(List<MoreInfoWebBean> officialWebsite) {
        this.officialWebsite = officialWebsite;
    }

    public void setCaptureDate(String captureDate) {
        this.captureDate = captureDate;
    }
}
