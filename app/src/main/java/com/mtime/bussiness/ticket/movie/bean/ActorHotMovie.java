package com.mtime.bussiness.ticket.movie.bean;

import com.mtime.base.bean.MBaseBean;

public class ActorHotMovie extends MBaseBean {

    private int movieId;
    private String movieCover;
    private String movieTitleCn;
    private String movieTitleEn;
    private float ratingFinal;
    private String type;
    private String roleName;
    private int ticketPrice;
    private boolean isTiket;

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public String getMovieCover() {
        return movieCover;
    }

    public void setMovieCover(String movieCover) {
        this.movieCover = movieCover;
    }

    public String getMovieTitleCn() {
        return movieTitleCn;
    }

    public void setMovieTitleCn(String movieTitleCn) {
        this.movieTitleCn = movieTitleCn;
    }

    public String getMovieTitleEn() {
        return movieTitleEn;
    }

    public void setMovieTitleEn(String movieTitleEn) {
        this.movieTitleEn = movieTitleEn;
    }

    public float getRatingFinal() {
        return ratingFinal;
    }

    public void setRatingFinal(float ratingFinal) {
        this.ratingFinal = ratingFinal;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public int getTicketPrice() {
        return ticketPrice;
    }

    public void setTicketPrice(int ticketPrice) {
        this.ticketPrice = ticketPrice;
    }

    public boolean isTiket() {
        return isTiket;
    }

    public void setTiket(boolean isTiket) {
        this.isTiket = isTiket;
    }

}
