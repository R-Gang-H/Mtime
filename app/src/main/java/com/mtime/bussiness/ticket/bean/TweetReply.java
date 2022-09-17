package com.mtime.bussiness.ticket.bean;

import com.mtime.base.bean.MBaseBean;

public class TweetReply extends MBaseBean {
    private int    reponseId;
    private String userImage;
    private int    userId;
    private String nickname;
    private String content;
    private long   stampTime;
    //评论图片url
    private String ceimg;

    public int getReponseId() {
        return reponseId;
    }

    public void setReponseId(int reponseId) {
        this.reponseId = reponseId;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getStampTime() {
        return stampTime;
    }

    public void setStampTime(long stampTime) {
        this.stampTime = stampTime;
    }
    public String getCeimg() {
        return ceimg;
    }

    public void setCeimg(String ceimg) {
        this.ceimg = ceimg;
    }
}
