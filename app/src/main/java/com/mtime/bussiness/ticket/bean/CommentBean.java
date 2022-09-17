package com.mtime.bussiness.ticket.bean;

import com.mtime.base.bean.MBaseBean;

import java.io.Serializable;

/**
 * 评论信息bean（影人/影院）
 * 
 * @author ye
 * 
 */
public class CommentBean extends MBaseBean implements Serializable {
    private static final long serialVersionUID = -8049887841666262892L;

    private int userId;
    private String nickname;
    private String userImage; // 头像图片地址
    // private String headUrl;
    private String address; // 用户所在地
    private int mVPType;
    private String rating;
    private int stampTime; // 评论时间戳
    private int enterTime;
    private String content;
    private int tweetId; //微影评Id (影人)
    private int commentCount; //回复数 (影人)
    private int topicId; //留言Id (影院)
    private int replyCount; //回复数 (影院)
    private int totalPraise;
    private boolean isPraise;

    public String getUserImage() {
	return userImage;
    }

    public void setUserImage(final String userImage) {
	this.userImage = userImage;
    }

    public String getNickname() {
	return nickname;
    }

    /**
     * 返回“用户昵称（城市）”格式的字符串，如： 惠子（广州） 注：昵称最大长度为10，如果城市值为空，则只返回昵称
     */
    public String getNicknameAndAddress() {
	String nameAndAddress = null;
	if ((nickname != null) && (nickname.length() > 10)) {
	    nameAndAddress = nickname.substring(0, 7) + "...";
	} else {
	    nameAndAddress = nickname;
	}
	if ((address != null) && !"".equals(address.trim())) {
	    nameAndAddress = nameAndAddress + "（" + address + "）";
	}
	return nameAndAddress;
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

    public String getRating() {
	return rating;
    }

    public double getRatingDouble() {
	try {
	    return Double.parseDouble(rating);
	} catch (final Exception e) {
	    return 0.0;
	}
    }

    public void setRating(final String rating) {
	this.rating = rating;
    }

    public String getContent() {
	return content;
    }

    public void setContent(final String content) {
	this.content = content;
    }

    public int getUserId() {
	return userId;
    }

    public void setUserId(final int userId) {
	this.userId = userId;
    }

    public String getAddress() {
	return address;
    }

    public void setAddress(final String address) {
	this.address = address;
    }

    public int getmVPType() {
	return mVPType;
    }

    public void setmVPType(final int mVPType) {
	this.mVPType = mVPType;
    }

    public int getStampTime() {
	return stampTime;
    }

    public void setStampTime(final int stampTime) {
	this.stampTime = stampTime;
    }

    public int getTweetId() {
        return tweetId;
    }

    public void setTweetId(int tweetId) {
        this.tweetId = tweetId;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public int getTopicId() {
        return topicId;
    }

    public void setTopicId(int topicId) {
        this.topicId = topicId;
    }

    public int getReplyCount() {
        return replyCount;
    }

    public void setReplyCount(int replyCount) {
        this.replyCount = replyCount;
    }

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

    public int getEnterTime() {
        return enterTime;
    }

    public void setEnterTime(int enterTime) {
        this.enterTime = enterTime;
    }
    
}