package com.mtime.bussiness.ticket.movie.bean;

import com.mtime.base.bean.MBaseBean;

import java.util.List;

public class OrderMovieInfo extends MBaseBean {

    private String director;
    private List<String> actors;
    private String releaseTime;
    private String rLocation;
    private boolean isEggHunt;
    private String movieId;
    private String image;
    private float ratingFinal;
    
    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public List<String> getActors() {
        return actors;
    }

    public void setActors(List<String> actors) {
        this.actors = actors;
    }

    public String getReleaseTime() {
        return releaseTime;
    }

    public void setReleaseTime(String releaseTime) {
        this.releaseTime = releaseTime;
    }

    public String getrLocation() {
        return rLocation;
    }

    public void setrLocation(String rLocation) {
        this.rLocation = rLocation;
    }

    public boolean isEggHunt() {
        return isEggHunt;
    }

    public void setEggHunt(boolean isEggHunt) {
        this.isEggHunt = isEggHunt;
    }

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public float getRatingFinal() {
        return ratingFinal;
    }

    public void setRatingFinal(float ratingFinal) {
        this.ratingFinal = ratingFinal;
    }
    
}
