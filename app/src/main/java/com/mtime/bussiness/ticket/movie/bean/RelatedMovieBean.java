package com.mtime.bussiness.ticket.movie.bean;

import com.mtime.base.bean.MBaseBean;

/**
 * Created by vivian.wei on 15/10/30.
 * 关联影片Bean
 */
public class RelatedMovieBean extends MBaseBean {

    private int movieID;
    private String title;
    private String nameEn;
    private String rating;
    private String img;
    private String year;
    private int directorId1;
    private String director1;
    private int actor1Id;
    private String actor1;
    private int actor2Id;
    private String actor2;
    private String releaseDate;
    private String releaseArea;

    // 自定义字段
    private boolean isGroupEnd;

    public void setMovieID(int movieID) {
        this.movieID = movieID;
    }

    public int getMovieID() {
        return movieID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNameEn() {
        return nameEn;
    }

    public void setNameEn(String nameEn) {
        this.nameEn = nameEn;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getYear() {
        return year;
    }

    public void setActor1(String actor1) {
        this.actor1 = actor1;
    }

    public int getActor1Id() {
        return actor1Id;
    }

    public int getDirectorId1() {
        return directorId1;
    }

    public void setDirectorId1(int directorId1) {
        this.directorId1 = directorId1;
    }

    public String getDirector1() {
        return director1;
    }

    public void setDirector1(String director1) {
        this.director1 = director1;
    }

    public void setActor1Id(int actor1Id) {
        this.actor1Id = actor1Id;
    }

    public String getActor1() {
        return actor1;
    }

    public int getActor2Id() {
        return actor2Id;
    }

    public void setActor2Id(int actor2Id) {
        this.actor2Id = actor2Id;
    }

    public String getActor2() {
        return actor2;
    }

    public void setActor2(String actor2) {
        this.actor2 = actor2;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getReleaseArea() {
        return releaseArea;
    }

    public void setReleaseArea(String releaseArea) {
        this.releaseArea = releaseArea;
    }

    public boolean isGroupEnd() {
        return isGroupEnd;
    }

    public void setGroupEnd(boolean groupEnd) {
        isGroupEnd = groupEnd;
    }
}
