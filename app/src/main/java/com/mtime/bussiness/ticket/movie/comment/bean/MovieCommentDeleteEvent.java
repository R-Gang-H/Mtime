package com.mtime.bussiness.ticket.movie.comment.bean;

import com.mtime.base.bean.MBaseBean;

/**
 * Created by <a href="mailto:wangkunlin1992@gmail.com">Wang kunlin</a>
 * <p>
 * On 2019-10-10
 */
public class MovieCommentDeleteEvent extends MBaseBean {

    public MovieCommentDeleteEvent(boolean isLong, long commentId) {
        this.isLong = isLong;
        this.commentId = commentId;
    }

    public boolean isLong = false;

    public long commentId;
}
