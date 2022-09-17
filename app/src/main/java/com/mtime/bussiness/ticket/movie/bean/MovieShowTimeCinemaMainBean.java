package com.mtime.bussiness.ticket.movie.bean;

import com.mtime.base.bean.MBaseBean;

import java.util.List;

public class MovieShowTimeCinemaMainBean extends MBaseBean {
    private String noticeNotOwn;                // 影城增加「非自营」
    private List<MovieShowTimeCinemaBean> cs;

    public String getNoticeNotOwn() {
        return noticeNotOwn;
    }

    public void setNoticeNotOwn(String noticeNotOwn) {
        this.noticeNotOwn = noticeNotOwn;
    }

    public List<MovieShowTimeCinemaBean> getCs() {
	return cs;
    }

    public void setCs(final List<MovieShowTimeCinemaBean> cs) {
	this.cs = cs;
    }
}
