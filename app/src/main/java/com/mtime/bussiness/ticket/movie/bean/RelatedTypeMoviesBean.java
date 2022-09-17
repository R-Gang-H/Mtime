package com.mtime.bussiness.ticket.movie.bean;

import com.mtime.base.bean.MBaseBean;

import java.util.List;

/**
 * Created by vivian.wei on 15/10/30.
 * 指定关联类型的关联影片列表
 */
public class RelatedTypeMoviesBean extends MBaseBean {

    // 关联类型中文名
    private String typeName;
    // 关联类型英文名
    private String typeEn;
    // 关联影片列表
    private List<RelatedMovieBean> movies;

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeEn(String typeEn) {
        this.typeEn = typeEn;
    }

    public String getTypeEn() {
        return typeEn;
    }

    public void setMovies(List<RelatedMovieBean> movies) {
        this.movies = movies;
    }

    public List<RelatedMovieBean> getMovies() {
        return movies;
    }
}
