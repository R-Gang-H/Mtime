package com.mtime.bussiness.video.bean;

import com.mtime.bussiness.information.bean.ArticleRelatedMoviesBean;

/**
 * Created by mtime on 2017/10/16.
 */

public class CommentInfoTitleBean extends CommentBean.CommentItemeBean {

    private String movieName;
    private String playCount;
    private long pulishTime;
    private ArticleRelatedMoviesBean relatedMovie;

    public ArticleRelatedMoviesBean getRelatedMovie() {
        return relatedMovie;
    }

    public void setRelatedMovie(ArticleRelatedMoviesBean relatedMovie) {
        this.relatedMovie = relatedMovie;
    }

    public String getMovieName() {
        return movieName;
    }

    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }

    public String getPlayCount() {
        return playCount;
    }

    public void setPlayCount(String playCount) {
        this.playCount = playCount;
    }

    public long getPulishTime() {
        return pulishTime;
    }

    public void setPulishTime(long pulishTime) {
        this.pulishTime = pulishTime;
    }
}
