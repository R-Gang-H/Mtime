package com.mtime.bussiness.ticket.movie.comment.bean;

/**
 * Created by <a href="mailto:wangkunlin1992@gmail.com">Wang kunlin</a>
 * <p>
 * On 2019-05-23
 */
public class UnknownCommentBean extends BaseCommentBean {

    UnknownCommentBean() {
        super(TYPE_UNKNOWN);
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public BaseCommentBean copy() {
        return new UnknownCommentBean();
    }
}
