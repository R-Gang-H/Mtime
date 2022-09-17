package com.mtime.bussiness.ticket.movie.bean;

import com.mtime.base.bean.MBaseBean;

import java.util.List;

/**
 * 用户想看电影ids
 * Created by lsmtime on 17/4/6.
 */

public class WantSeeFilmBean extends MBaseBean {
    private List<Integer> movieIds;

    public List<Integer> getMovieIds() {
        return movieIds;
    }

    public void setMovieIds(List<Integer> movieIds) {
        this.movieIds = movieIds;
    }

}
