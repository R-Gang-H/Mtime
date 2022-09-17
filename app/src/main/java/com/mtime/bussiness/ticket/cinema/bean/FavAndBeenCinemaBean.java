package com.mtime.bussiness.ticket.cinema.bean;

import com.mtime.base.bean.MBaseBean;

/**
 * Created by vivian.wei on 2017/10/16.
 * 用户收藏、上次去过影院
 */

public class FavAndBeenCinemaBean extends MBaseBean {

    private int fId;        // 收藏id
    private int cinemaId;   // 影院id
    private String name;    // 影院名称
    // TODO: 2017/10/20 影讯迁移到接口  http://api.m.mtime.cn/Showtime/LocationMovieShowtimes.api
//    private List<CinemaShowtimeBean> list = new ArrayList<>(); // 影院场次列表

    public int getfId() {
        return fId;
    }

    public void setfId(int fId) {
        this.fId = fId;
    }

    public int getCinemaId() {
        return cinemaId;
    }

    public void setCinemaId(int cinemaId) {
        this.cinemaId = cinemaId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
