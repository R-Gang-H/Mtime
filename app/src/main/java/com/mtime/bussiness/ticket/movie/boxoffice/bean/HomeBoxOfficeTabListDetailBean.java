package com.mtime.bussiness.ticket.movie.boxoffice.bean;

import com.helen.obfuscator.IObfuscateKeepAll;
import com.kk.taurus.uiframe.i.HolderData;
import com.mtime.base.bean.MBaseBean;

import java.util.List;

public class HomeBoxOfficeTabListDetailBean extends MBaseBean implements IObfuscateKeepAll, HolderData {
    private HomeBoxOfficeTabListDetailTopListBean topList;
    private List<HomeBoxOfficeTabListDetailMovieBean> movies;
    private List<HomeBoxOfficeTabListDetailPersonBean> persons;
    private int type;//类型  1:影片  2:影人

    public List<HomeBoxOfficeTabListDetailPersonBean> getPersons() {
        return persons;
    }

    public void setPersons(final List<HomeBoxOfficeTabListDetailPersonBean> persons) {
        this.persons = persons;
    }

    public HomeBoxOfficeTabListDetailTopListBean getTopList() {
        return topList;
    }

    public void setTopList(final HomeBoxOfficeTabListDetailTopListBean topList) {
        this.topList = topList;
    }

    public List<HomeBoxOfficeTabListDetailMovieBean> getMovies() {
        return movies;
    }

    public void setMovies(final List<HomeBoxOfficeTabListDetailMovieBean> movies) {
        this.movies = movies;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
