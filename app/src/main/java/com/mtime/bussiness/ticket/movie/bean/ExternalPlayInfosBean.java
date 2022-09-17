package com.mtime.bussiness.ticket.movie.bean;

import com.mtime.base.bean.MBaseBean;

import java.util.List;

/**
 * Created by zhuqiguang on 2018/6/26.
 * website www.zhuqiguang.cn
 * http://wiki.inc-mtime.com/pages/viewpage.action?pageId=160071783
 */
public class ExternalPlayInfosBean extends MBaseBean {
    private ExternalPlayRelatedBean relatedMovie;

    public ExternalPlayRelatedBean getRelatedMovie() {
        return relatedMovie;
    }

    public void setRelatedMovie(ExternalPlayRelatedBean relatedMovie) {
        this.relatedMovie = relatedMovie;
    }

    public List<ExternalPlayPlayListBean> getPlaylist() {
        return playlist;
    }

    public void setPlaylist(List<ExternalPlayPlayListBean> playlist) {
        this.playlist = playlist;
    }

    private List<ExternalPlayPlayListBean> playlist;
}
