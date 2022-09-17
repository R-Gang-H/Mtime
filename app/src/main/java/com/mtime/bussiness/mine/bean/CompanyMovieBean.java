package com.mtime.bussiness.mine.bean;

import com.mtime.base.bean.MBaseBean;

/**
 * Created by mtime on 15/10/26.
 */
public class CompanyMovieBean extends MBaseBean {
    private int id;
    private String name;
    private String nameEn;
    private String rating;
    private String img;
    private int year;
    private String director;
    private String actor1;
    private String actor2;
    private int directorId;
    private int actor1Id;
    private int actor2Id;
    private String releaseDate;
    private String releaseArea;

    public String getReleaseArea() {
        return releaseArea;
    }

    public void setReleaseArea(String releaseArea) {
        this.releaseArea = releaseArea;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getRating() {
        return rating;
    }

    public String getNameEn() {
        return nameEn;
    }

    public String getImg() {
        return img;
    }

    public int getYear() {
        return year;
    }

    public String getDirector() {
        return director;
    }

    public String getActor1() {
        return actor1;
    }

    public int getDirectorId() {
        return directorId;
    }

    public String getActor2() {
        return actor2;
    }

    public int getActor1Id() {
        return actor1Id;
    }

    public int getActor2Id() {
        return actor2Id;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNameEn(String nameEn) {
        this.nameEn = nameEn;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public void setActor1(String actor1) {
        this.actor1 = actor1;
    }

    public void setDirectorId(int directorId) {
        this.directorId = directorId;
    }

    public void setActor2(String actor2) {
        this.actor2 = actor2;
    }

    public void setActor1Id(int actor1Id) {
        this.actor1Id = actor1Id;
    }

    public void setActor2Id(int actor2Id) {
        this.actor2Id = actor2Id;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }
}
