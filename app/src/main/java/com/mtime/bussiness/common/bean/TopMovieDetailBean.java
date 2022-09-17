package com.mtime.bussiness.common.bean;

import com.mtime.base.bean.MBaseBean;
import com.mtime.bussiness.ticket.movie.bean.TopList;

import java.util.List;

public class TopMovieDetailBean extends MBaseBean {
    private TopList topList;
    private List<TopMovy> movies;
    private int type;//类型  1:影片  2:影人
    private List<TopParser> persons;

    public List<TopParser> getPersons() {
        return persons;
    }

    public void setPersons(final List<TopParser> persons) {
        this.persons = persons;
    }

    public TopList getTopList() {
        return topList;
    }

    public void setTopList(final TopList topList) {
        this.topList = topList;
    }

    public List<TopMovy> getMovies() {
        return movies;
    }

    public void setMovies(final List<TopMovy> movies) {
        this.movies = movies;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
