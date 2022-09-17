package com.mtime.bussiness.ticket.movie.bean;

import com.mtime.base.bean.MBaseBean;

/**
 * Created by LEE on 8/8/16.
 */
public class MovieBaseItem extends MBaseBean {
    private int movieId; //电影id编号
    private String nameCN;   //电影中文名称
    private String nameEN;  //电影英文名称

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public String getNameCN() {
        return nameCN;
    }

    public void setNameCN(String nameCN) {
        this.nameCN = nameCN;
    }

    public String getNameEN() {
        return nameEN;
    }

    public void setNameEN(String nameEN) {
        this.nameEN = nameEN;
    }
}
