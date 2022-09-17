package com.mtime.bussiness.ticket.cinema.bean;

import com.mtime.base.bean.MBaseBean;

import java.util.List;

public class MovieTimeChildMainBean extends MBaseBean {
    private String moviekey;//格式:movieId_日志 例如：38558_2014-10-30
    private boolean showDatesisCoupon; //影讯上标注的是否优惠标签
    private List<ShowtimeJsonBean> list;

    public boolean isShowDatesisCoupon() {
        return showDatesisCoupon;
    }

    public void setShowDatesisCoupon(boolean showDatesisCoupon) {
        this.showDatesisCoupon = showDatesisCoupon;
    }

    public String getMoviekey() {
        return moviekey;
    }

    public void setMoviekey(String moviekey) {
        this.moviekey = moviekey;
    }

    public List<ShowtimeJsonBean> getList() {
        return list;
    }

    public void setList(List<ShowtimeJsonBean> list) {
        this.list = list;
    }
}
