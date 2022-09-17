package com.mtime.bussiness.mine.bean;

import com.mtime.base.bean.MBaseBean;

public class CommentAndReplyListBean  extends MBaseBean {
    private String nickName;
    private String img;
    private String body;
    private Double rating;
    private int tweetId;
    private long time;
    private String relatedName;
    private String relateImg;
    private int relatedId;
    private long relatedTime;
    private String relatedContent;
    private int relatedTwitterId;

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public int getTweetId() {
        return tweetId;
    }

    public void setTweetId(int tweetId) {
        this.tweetId = tweetId;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getRelatedName() {
        return relatedName;
    }

    public void setRelatedName(String relatedName) {
        this.relatedName = relatedName;
    }

    public String getRelateImg() {
        return relateImg;
    }

    public void setRelateImg(String relateImg) {
        this.relateImg = relateImg;
    }

    public int getRelatedId() {
        return relatedId;
    }

    public void setRelatedId(int relatedId) {
        this.relatedId = relatedId;
    }

    public long getRelatedTime() {
        return relatedTime;
    }

    public void setRelatedTime(long relatedTime) {
        this.relatedTime = relatedTime;
    }

    public String getRelatedContent() {
        return relatedContent;
    }

    public void setRelatedContent(String relatedContent) {
        this.relatedContent = relatedContent;
    }

    public int getRelatedTwitterId() {
        return relatedTwitterId;
    }

    public void setRelatedTwitterId(int relatedTwitterId) {
        this.relatedTwitterId = relatedTwitterId;
    }
}
