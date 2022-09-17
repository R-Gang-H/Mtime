//package com.mtime.bussiness.ticket.movie.bean;
//
//import com.mtime.base.bean.MBaseBean;
//
//import java.io.Serializable;
//
///**
// * 提醒(上映提醒)用到的实体bean
// * (2021.10.21提示功能去掉)
// * @author ye
// *
// */
//public class RemindBean extends MBaseBean implements Serializable {
//    private static final long serialVersionUID = -1986547881754880571L;
//
//    private String id;
//    private String releaseTime; // 格式：2011-11-12
//    private String name;
//    private boolean showed;
//    private long date;
//
//    public long getDate() {
//	return date;
//    }
//
//    public void setDate(final long date) {
//	this.date = date;
//    }
//
//    public RemindBean(final String id, final String releaseTime,
//	    final String name, final long d) {
//	super();
//	this.id = id;
//	this.releaseTime = releaseTime;
//	this.name = name;
//	date = d;
//    }
//
//    public boolean isShowed() {
//	return showed;
//    }
//
//    public void setShowed(final boolean showed) {
//	this.showed = showed;
//    }
//
//    public String getName() {
//	return name;
//    }
//
//    public void setName(final String name) {
//	this.name = name;
//    }
//
//    public RemindBean(final String id, final String releaseTime) {
//	this.id = id;
//	this.releaseTime = releaseTime;
//    }
//
//    public String getId() {
//	return id;
//    }
//
//    public void setId(final String id) {
//	this.id = id;
//    }
//
//    public String getReleaseTime() {
//	return releaseTime;
//    }
//
//    public void setReleaseTime(final String releaseTime) {
//	this.releaseTime = releaseTime;
//    }
//}
