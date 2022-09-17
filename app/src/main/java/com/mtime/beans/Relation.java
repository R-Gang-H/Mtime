package com.mtime.beans;

import com.mtime.base.bean.MBaseBean;

import java.io.Serializable;

public class Relation extends MBaseBean implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private int type;
    private int id;
    private String name;
    private String image;
    private String year;
    private double rating;
    private int scoreCount;
    private String releaseDate;
    private String relaseLocation;
    private String nameEn;

    public int getType() {
	return type;
    }

    public void setType(final int type) {
	this.type = type;
    }

    public int getId() {
	return id;
    }

    public void setId(final int id) {
	this.id = id;
    }

    public String getName() {
	return name;
    }

    public void setName(final String name) {
	this.name = name;
    }

    public String getImage() {
	return image;
    }

    public void setImage(final String image) {
	this.image = image;
    }

    public String getYear() {
	return year;
    }

    public void setYear(final String year) {
	this.year = year;
    }

    public double getRating() {
	return rating;
    }

    public void setRating(final double rating) {
	this.rating = rating;
    }

    public int getScoreCount() {
	return scoreCount;
    }

    public void setScoreCount(final int scoreCount) {
	this.scoreCount = scoreCount;
    }

    public String getReleaseDate() {
	return releaseDate;
    }

    public void setReleaseDate(final String releaseDate) {
	this.releaseDate = releaseDate;
    }

    public String getRelaseLocation() {
	return relaseLocation;
    }

    public void setRelaseLocation(final String relaseLocation) {
	this.relaseLocation = relaseLocation;
    }

    public String getNameEn() {
	return nameEn;
    }

    public void setNameEn(final String nameEn) {
	this.nameEn = nameEn;
    }
}
