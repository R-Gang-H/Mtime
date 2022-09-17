package com.mtime.bussiness.ticket.movie.bean;

import com.mtime.base.bean.MBaseBean;

import java.util.List;

/**
 * 影片媒体评论列表Bean
 */
public class MediaReviewBean extends MBaseBean {
    List<Medias> medias;

    public List<Medias> getMedias() {
        return medias;
    }

    public void setMedias(List<Medias> medias) {
        this.medias = medias;
    }
}
