package com.mtime.bussiness.ticket.movie.comment.bean;

import com.mtime.base.bean.MBaseBean;

/**
 * Created by <a href="mailto:wangkunlin1992@gmail.com">Wang kunlin</a>
 * <p>
 * On 2019-10-10
 */
public class PublishMovieCommentEvent extends MBaseBean {

    public boolean isLong = false;

    public PublishMovieCommentEvent(boolean isLong) {
        this.isLong = isLong;
    }
}
