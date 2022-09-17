package com.mtime.bussiness.information.bean;

import com.mtime.base.bean.MBaseBean;

import java.util.ArrayList;
import java.util.List;

public class NewsCommentItemBean extends MBaseBean {
    private int id;
    private String nickname;
    private String userImage;
    private String date;
    private String content;
    private int mVPType;
    private String fromApp = "";
    private long timestamp;
    private int commentCount;//评论总数
    private List<NewsCommentItemReplyBean> replies = new ArrayList<NewsCommentItemReplyBean>();

    public int getId() {
        return id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(final String nickname) {
        this.nickname = nickname;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(final String userImage) {
        this.userImage = userImage;
    }

    public String getDate() {
        return date;
    }

    public void setDate(final String date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(final String content) {
        this.content = content;
    }

    public int getMVPType() {
        return mVPType;
    }

    public void setMVPType(final int mVPType) {
        this.mVPType = mVPType;
    }

    public List<NewsCommentItemReplyBean> getReplies() {
        return replies;
    }

    public void setReplies(final List<NewsCommentItemReplyBean> replies) {
        this.replies = replies;
    }

    public String getFromApp() {
        return fromApp;
    }

    public void setFromApp(String fromApp) {
        this.fromApp = fromApp;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getmVPType() {
        return mVPType;
    }

    public void setmVPType(int mVPType) {
        this.mVPType = mVPType;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }
}
