package com.mtime.bussiness.ticket.movie.bean;

import com.mtime.base.bean.MBaseBean;

import java.util.List;

/**
 * 作品年表
 * 
 * @author ye
 */
public class FilmographyBean extends MBaseBean {
    private long id;
    private String image; // 图片url
    private String name;
    private String year; // 年份，如2012
    private String rating; // 评分，如8.9
    private List<NameBean> offices; // 职位，返回数组，因为可能会有多个职位。
    private List<NameBean> personates; // 饰演，返回数组，因为可能会有多个饰演
    private List<Award> awards; // 影片所获奖项，返回数组，会有多个奖项
    private String releaseDate; // 2010-12-12, //月、日可能为0
    private String releaseCountry; // 上映国家

    public long getId() {
	return id;
    }

    public void setId(final long id) {
	this.id = id;
    }

    public String getImage() {
	return image;
    }

    public void setImage(final String image) {
	this.image = image;
    }

    public String getName() {
	return name;
    }

    public String getNameAndYear() {
	final StringBuilder result = new StringBuilder();
	if ((name != null) && (name.trim().length() > 0)) {
	    result.append(name);
	}

	if ((year != null) && (year.trim().length() > 0)) {
	    if (result.length() > 0) {
		result.append(" (").append(year).append(")");
	    } else {
		result.append(year);
	    }
	}
	return result.toString();
    }

    public void setName(final String name) {
	this.name = name;
    }

    public String getYear() {
	return year;
    }

    public void setYear(final String year) {
	this.year = year;
    }

    public String getRating() {
	return rating;
    }

    public void setRating(final String rating) {
	this.rating = rating;
    }

    public List<NameBean> getOffices() {
	return offices;
    }

    public String getOfficesString() {
	if ((offices == null) || (offices.size() == 0)) {
	    return null;
	}

	final StringBuilder result = new StringBuilder();
	for (final NameBean bean : offices) {
	    final String name = bean.getName();
	    if ((name != null) && (name.trim().length() > 0)) {
		result.append(name).append(" / ");
	    }
	}
	if (result.length() > 3) {
	    return result.substring(0, result.length() - 3).trim();
	}
	return result.toString().trim();
    }

    public void setOffices(final List<NameBean> offices) {
	this.offices = offices;
    }

    public String getPersonateString() {
	if ((personates == null) || (personates.size() == 0)) {
	    return null;
	}

	final StringBuilder result = new StringBuilder();
	for (final NameBean bean : personates) {
	    final String name = bean.getName();
	    if ((name != null) && (name.trim().length() > 0)) {
		result.append(name).append("， ");
	    }
	}
	if (result.length() > 2) {
	    return result.substring(0, result.length() - 2).trim();
	}
	return result.toString().trim();
    }

    public List<NameBean> getPersonates() {
	return personates;
    }

    public void setPersonates(final List<NameBean> personates) {
	this.personates = personates;
    }

    public List<Award> getAwards() {
	return awards;
    }

    public void setAwards(final List<Award> awards) {
	this.awards = awards;
    }

    public String getReleaseDate() {
	return releaseDate;
    }

    public void setReleaseDate(final String releaseDate) {
	this.releaseDate = releaseDate;
    }

    public String getReleaseCountry() {
	return releaseCountry;
    }

    public void setReleaseCountry(final String releaseCountry) {
	this.releaseCountry = releaseCountry;
    }
}
