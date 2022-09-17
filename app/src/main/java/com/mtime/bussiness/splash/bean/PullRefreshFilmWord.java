package com.mtime.bussiness.splash.bean;


import com.mtime.base.bean.MBaseBean;

/**
 * 下拉刷新的电影词
 * 用于{@link com.mtime.mtmovie.widgets.pullrefresh.PullRefreshHeaderView}
 */
public class PullRefreshFilmWord extends MBaseBean {
    private String movieName;
    private String word;

    public String getMovieName() {
        if (movieName == null) {
            return "";
        } else {
            return movieName;
        }
    }

    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }

    public String getWord() {
        if (word == null) {
            return "";
        } else {
            return word;
        }
    }

    public void setWord(String word) {
        this.word = word;
    }
}
