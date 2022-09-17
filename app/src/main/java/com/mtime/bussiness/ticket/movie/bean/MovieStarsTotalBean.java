package com.mtime.bussiness.ticket.movie.bean;

import com.mtime.base.bean.MBaseBean;

import java.util.List;

public class MovieStarsTotalBean extends MBaseBean {
    private List<MovieStarsType> types;

    public List<MovieStarsType> getMovieStarsTypes() {
	return types;
    }

    public void setMovieStarsTypes(final List<MovieStarsType> types) {
	this.types = types;
    }
}
