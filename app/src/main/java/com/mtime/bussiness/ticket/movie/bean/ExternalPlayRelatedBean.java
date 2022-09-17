package com.mtime.bussiness.ticket.movie.bean;

import com.mtime.base.bean.MBaseBean;

/**
 * Created by zhuqiguang on 2018/6/26.
 * website www.zhuqiguang.cn
 */
public class ExternalPlayRelatedBean extends MBaseBean {

    /**
     * movieId : 122
     * name : n
     * nameEn : xxxxx
     * img : http: //img31.mtime.cn/mt/244/58244/58244_96X128.jpg
     * rating : 7.8
     * movieType : 纪录片
     * duration : 120分钟
     */

    private int movieId;
    private String name;
    private String nameEn;
    private String img;
    private String rating;
    private String movieType;
    private String duration;
    private boolean isFilter;

    public boolean isFilter() {
        return isFilter;
    }

    public void setFilter(boolean filter) {
        isFilter = filter;
    }

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameEn() {
        return nameEn;
    }

    public void setNameEn(String nameEn) {
        this.nameEn = nameEn;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getMovieType() {
        return movieType;
    }

    public void setMovieType(String movieType) {
        this.movieType = movieType;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
}
