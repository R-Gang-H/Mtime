package com.mtime.bussiness.ticket.movie.bean;

import com.mtime.base.bean.MBaseBean;

public class TweetParent extends MBaseBean {
    private int    tweetId;
    private String userImage;
    private int    userId;
    private String nickname;
    private float    rating;
    private String content;
    private long    stampTime;
    private int    commentCount;
    private int totalPraise;
    private boolean isPraise;
    //评论图片url
    private String ceimg;
    
    public int getTotalPraise() {
        return totalPraise;
    }

    public void setTotalPraise(int totalPraise) {
        this.totalPraise = totalPraise;
    }

    public boolean isPraise() {
        return isPraise;
    }

    public void setPraise(boolean isPraise) {
        this.isPraise = isPraise;
    }

    public int getTweetId() {
        return tweetId;
    }

    public void setTweetId(int tweetId) {
        this.tweetId = tweetId;
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

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
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

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }
    public String getCeimg() {
        return ceimg;
    }

    public void setCeimg(String ceimg) {
        this.ceimg = ceimg;
    }
}
