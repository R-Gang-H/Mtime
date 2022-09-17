package com.mtime.bussiness.ticket.movie.bean;

import com.mtime.base.bean.MBaseBean;

import java.util.List;

/**
 * Created by guangshun on 17/3/15.
 */

public class ShareImagesBean extends MBaseBean {
    /**
     * name : 金刚狼
     * nameEn : xxxxx
     * movieStills : [{"stillUrl":"XXXX"}]
     */

    private String name;
    private String nameEn;
    /**
     * stillUrl : XXXX
     */

    private List<MovieStillsBean> movieStills;

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

    public List<MovieStillsBean> getMovieStills() {
        return movieStills;
    }

    public void setMovieStills(List<MovieStillsBean> movieStills) {
        this.movieStills = movieStills;
    }

    public static class MovieStillsBean extends MBaseBean {
        private String stillUrl;

        public String getStillUrl() {
            return stillUrl;
        }

        public void setStillUrl(String stillUrl) {
            this.stillUrl = stillUrl;
        }
    }
}
