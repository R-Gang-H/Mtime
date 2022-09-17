package com.mtime.bussiness.ticket.cinema.bean;

import com.mtime.base.bean.MBaseBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vivian.wei on 2017/10/16.
 * 用户收藏、上次去过影院列表
 */

public class FavAndBeenCinemaListBean extends MBaseBean {

    private List<FavAndBeenCinemaBean> favoriteList = new ArrayList<>(); // 收藏影院列表
    private FavAndBeenCinemaBean beenCinema;   // 上次去过影院

    public List<FavAndBeenCinemaBean> getFavoriteList() {
        return favoriteList;
    }

    public void setFavoriteList(List<FavAndBeenCinemaBean> favoriteList) {
        this.favoriteList = favoriteList;
    }

    public FavAndBeenCinemaBean getBeenCinema() {
        return beenCinema;
    }

    public void setBeenCinema(FavAndBeenCinemaBean beenCinema) {
        this.beenCinema = beenCinema;
    }
}
