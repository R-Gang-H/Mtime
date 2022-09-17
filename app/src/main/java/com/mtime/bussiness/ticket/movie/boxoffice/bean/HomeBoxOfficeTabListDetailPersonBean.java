package com.mtime.bussiness.ticket.movie.boxoffice.bean;

import com.mtime.base.bean.MBaseBean;

public class HomeBoxOfficeTabListDetailPersonBean extends MBaseBean {
    private int id;
    private String nameCn;
    private String nameEn;
    private int rankNum;
    private String posterUrl;
    private double rating;
    private String sex;
    private int birthYear;
    private String birthDay;
    private String birthLocation;
    private String constellation;
    private String summary;
    private String remark;

    public String getRemark() {
	return remark;
    }

    public void setRemark(final String remark) {
	this.remark = remark;
    }

    public int getId() {
	return id;
    }

    public void setId(final int id) {
	this.id = id;
    }

    public String getNameCn() {
        if (nameCn == null) {
            return "";
        }
        return nameCn;
    }

    public void setNameCn(final String nameCn) {
	this.nameCn = nameCn;
    }

    public String getNameEn() {
	return nameEn;
    }

    public void setNameEn(final String nameEn) {
	this.nameEn = nameEn;
    }

    public int getRankNum() {
	return rankNum;
    }

    public String getFormatNum() {
	if (rankNum < 10) {
	    return String.format("0%d", rankNum);
	} else {
	    return String.valueOf(rankNum);
	}
    }

    public void setRankNum(final int rankNum) {
	this.rankNum = rankNum;
    }

    public String getPosterUrl() {
	return posterUrl;
    }

    public void setPosterUrl(final String posterUrl) {
	this.posterUrl = posterUrl;
    }

    public double getRating() {
	return rating;
    }

    public void setRating(final double rating) {
	this.rating = rating;
    }

    public String getSex() {
	return sex;
    }

    public void setSex(final String sex) {
	this.sex = sex;
    }

    public int getBirthYear() {
	return birthYear;
    }

    public void setBirthYear(final int birthYear) {
	this.birthYear = birthYear;
    }

    public String getBirthDay() {
	return birthDay;
    }

    public void setBirthDay(final String birthDay) {
	this.birthDay = birthDay;
    }

    public String getBirthLocation() {
	return birthLocation;
    }

    public void setBirthLocation(final String birthLocation) {
	this.birthLocation = birthLocation;
    }

    public String getConstellation() {
	return constellation;
    }

    public void setConstellation(final String constellation) {
	this.constellation = constellation;
    }

    public String getSummary() {
	return summary;
    }

    public void setSummary(final String summary) {
	this.summary = summary;
    }
}
