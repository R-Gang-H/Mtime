package com.mtime.bussiness.ticket.cinema.bean;

import com.mtime.base.bean.MBaseBean;

import java.util.List;

/**
 * Created by vivian.wei on 2018/7/24.
 */

public class OnlineCityCinemaListBean extends MBaseBean {

    private String noticeNotOwn;                // 影城增加「非自营」提示
    private List<CinemaBaseInfo> cinemaList;    // 影院列表

    public String getNoticeNotOwn() {
        return noticeNotOwn;
    }

    public void setNoticeNotOwn(String noticeNotOwn) {
        this.noticeNotOwn = noticeNotOwn;
    }

    public List<CinemaBaseInfo> getCinemaList() {
        return cinemaList;
    }

    public void setCinemaList(List<CinemaBaseInfo> cinemaList) {
        this.cinemaList = cinemaList;
    }
}
