package com.mtime.bussiness.ticket.movie.bean;

import android.text.TextUtils;

import com.mtime.base.bean.MBaseBean;

public class ActorNominates extends MBaseBean {
    
    private int sequenceNumber;
    private int movieId;
    private String festivalEventYear;
    private String awardName;
    private String movieTitle;
    private String movieTitleEn;
    private String movieYear;
    private String image;
    private String roleName;
    
    public int getSequenceNumber() {
        return sequenceNumber;
    }
    public void setSequenceNumber(int sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }
    public int getMovieId() {
        return movieId;
    }
    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }
    public String getFestivalEventYear() {
        return festivalEventYear;
    }
    public void setFestivalEventYear(String festivalEventYear) {
        this.festivalEventYear = festivalEventYear;
    }
    public String getAwardName() {
        return awardName;
    }
    public void setAwardName(String awardName) {
        this.awardName = awardName;
    }
    public String getMovieTitle() {
        return movieTitle;
    }
    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }
    public String getMovieTitleEn() {
        return movieTitleEn;
    }
    public void setMovieTitleEn(String movieTitleEn) {
        this.movieTitleEn = movieTitleEn;
    }
    public String getMovieYear() {
        return movieYear;
    }
    public void setMovieYear(String movieYear) {
        this.movieYear = movieYear;
    }
    public String getImage() {
        return image;
    }
    public void setImage(String image) {
        this.image = image;
    }
    public String getRoleName() {
        return roleName;
    }
    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
    public boolean isEmpty() {
        return 0 == sequenceNumber && 0 == movieId && TextUtils.isEmpty(festivalEventYear) && TextUtils.isEmpty(awardName) && TextUtils.isEmpty(movieTitle) && TextUtils.isEmpty(movieTitleEn) &&
                TextUtils.isEmpty(movieYear) && TextUtils.isEmpty(image) && TextUtils.isEmpty(roleName);
    }
}
