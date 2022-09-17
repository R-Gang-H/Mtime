package com.mtime.bussiness.ticket.movie.bean;

import com.mtime.base.bean.MBaseBean;

import java.util.List;

/**
 * Created by vivian.wei on 15/10/30.
 * 指定影片的关联电影列表Bean
 */
public class RelatedMoviesBean extends MBaseBean {

    //关联影片列表
    private List<RelatedTypeMoviesBean> list;

    public void setList(List<RelatedTypeMoviesBean> list) {
        this.list = list;
    }

    public List<RelatedTypeMoviesBean> getList() {
        return list;
    }
}
