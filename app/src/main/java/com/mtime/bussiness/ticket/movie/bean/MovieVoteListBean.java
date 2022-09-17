package com.mtime.bussiness.ticket.movie.bean;

import com.mtime.base.bean.MBaseBean;

import java.util.ArrayList;
import java.util.List;

public class MovieVoteListBean extends MBaseBean {
    private List<MovieVoteBean> list = new ArrayList<MovieVoteBean>();

    public List<MovieVoteBean> getList() {
        return list;
    }

    public void setList(List<MovieVoteBean> list) {
        this.list = list;
    }
}
