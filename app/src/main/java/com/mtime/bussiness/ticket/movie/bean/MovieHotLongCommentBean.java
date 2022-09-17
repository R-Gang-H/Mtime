// JSON Java Class Generator
// Written by Bruce Bao
// Used for API: 
package com.mtime.bussiness.ticket.movie.bean;

import com.mtime.base.bean.MBaseBean;

import java.io.Serializable;

public class MovieHotLongCommentBean extends MBaseBean implements Serializable {
	private static final long serialVersionUID = 7118878215825925753L;
	private int id;
	private String title;
	private String content;
	private String nickname;
	private String headurl;
	private String location;
	private long modifyTime;
	private double rating;
	private boolean isWantSee;
	private int commentCount; // 回复次数
	
	public void setIsWantSee(boolean isWantSee){
	    this.isWantSee = isWantSee;
	}
	
	public boolean isIsWantSee(){
	    return isWantSee;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public void setRating(double rating) {
		this.rating = rating;
	}
	public double getRatin() {
		return rating;
	}

	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getHeadurl() {
		return headurl;
	}

	public void setHeadurl(String headurl) {
		this.headurl = headurl;
	}


	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public long getModifyTime() {
		return modifyTime;
	}

	public void setModifyTime(long modifyTime) {
		this.modifyTime = modifyTime;
	}

	public int getCommentCount() {
		return commentCount;
	}

	public void setCommentCount(int commentCount) {
		this.commentCount = commentCount;
	}
}
