package com.mtime.bussiness.information.bean;

import com.mtime.base.bean.MBaseBean;

public class NewsCommentItemReplyBean extends MBaseBean {
    private int    id;
    private String nickname;
    private int    mVPType;
    private String userImage;
    private String date;
    private String content;
    private long   timestamp;
    
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
    
    public int getMVPType() {
        return mVPType;
    }
    
    public void setMVPType(final int mVPType) {
        this.mVPType = mVPType;
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
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    
}
