package com.mtime.bussiness.video.bean;

import com.mtime.base.bean.MBaseBean;

/**
 * 评论或回复
 * Created by lys on 17/8/17.
 */

public class CommentReViewBean extends MBaseBean {
    private int bizCode;
    private String bizMsg;
    private int commentId;

    public int getBizCode() {
        return bizCode;
    }

    public void setBizCode(int bizCode) {
        this.bizCode = bizCode;
    }

    public String getBizMsg() {
        return bizMsg;
    }

    public void setBizMsg(String bizMsg) {
        this.bizMsg = bizMsg;
    }

    public int getCommentId() {
        return commentId;
    }

    public void setCommentId(int commentId) {
        this.commentId = commentId;
    }
}
