package com.mtime.bussiness.ticket.movie.boxoffice.bean;

import com.mtime.base.bean.MBaseBean;

public class HomeBoxOfficeTabListDetailMovieBean extends MBaseBean {
    private int id;
    private String name;
    private String nameEn;
    private int rankNum;
    private String posterUrl;
    private int decade;
    private double rating;
    private String releaseDate;
    private String releaseLocation;
    private String movieType;
    private String director;
    private String actor;
    private String remark;
    private String weekBoxOffice;
    private String totalBoxOffice;
    private boolean isTicket;
    private boolean isPresell;
    private String todayBoxOffice;
    private String totalBoxOfficeNew;

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

    public int getDecade() {
        return decade;
    }

    public void setDecade(final int decade) {
        this.decade = decade;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(final double rating) {
        this.rating = rating;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(final String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getReleaseLocation() {
        return releaseLocation;
    }

    public void setReleaseLocation(final String releaseLocation) {
        this.releaseLocation = releaseLocation;
    }

    public String getMovieType() {
        return movieType;
    }

    public void setMovieType(final String movieType) {
        this.movieType = movieType;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(final String director) {
        this.director = director;
    }

    public String getActor() {
        return actor;
    }

    public void setActor(final String actor) {
        this.actor = actor;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(final String remark) {
        this.remark = remark;
    }

    public String getWeekBoxOffice() {
        return weekBoxOffice;
    }

    public void setWeekBoxOffice(String weekBoxOffice) {
        this.weekBoxOffice = weekBoxOffice;
    }

    public String getTotalBoxOffice() {
        return totalBoxOffice;
    }

    public void setTotalBoxOffice(String totalBoxOffice) {
        this.totalBoxOffice = totalBoxOffice;
    }

    public String getNameEn() {
        return nameEn;
    }

    public void setNameEn(String nameEn) {
        this.nameEn = nameEn;
    }

    public boolean getIsTicket() {
        return isTicket;
    }

    public void setIsTicket(boolean isTicket) {
        this.isTicket = isTicket;
    }

    public boolean getIsPresell() {
        return isPresell;
    }

    public void setIsPresell(boolean isPresell) {
        this.isPresell = isPresell;
    }

    public String getTodayBoxOffice() {
        if (todayBoxOffice == null) {
            return "";
        }
        return todayBoxOffice;
    }

    public void setTodayBoxOffice(String todayBoxOffice) {
        this.todayBoxOffice = todayBoxOffice;
    }

    public String getTotalBoxOfficeNew() {
        if (totalBoxOfficeNew == null) {
            return "";
        }
        return totalBoxOfficeNew;
    }

    public void setTotalBoxOfficeNew(String totalBoxOfficeNew) {
        this.totalBoxOfficeNew = totalBoxOfficeNew;
    }
}
