package com.mtime.bussiness.ticket.movie.bean;

import com.mtime.base.bean.MBaseBean;

/**
 * Created by guangshun on 16-6-14.
 */
public class MovieVoteResultBean extends MBaseBean {
    private MovieVoteListBean data;

    public void setData(MovieVoteListBean data) {
        this.data = data;
    }

    public MovieVoteListBean getData() {
        return data;
    }

}
