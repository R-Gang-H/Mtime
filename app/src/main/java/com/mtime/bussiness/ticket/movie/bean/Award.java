package com.mtime.bussiness.ticket.movie.bean;

import com.mtime.base.bean.MBaseBean;

/**
 * 获奖记录bean（影人-作品年表）
 * 
 * @author ye
 * 
 */
public class Award extends MBaseBean {
    private String eventName; // 电影节名称,如：第70届奥斯卡金像奖
    private String year; // 电影节年份，如： 1997
    private String awardName; // 奖项名称，如： 最佳影片

    public String getEventName() {
	return eventName;
    }

    public void setEventName(final String eventName) {
	this.eventName = eventName;
    }

    public String getYear() {
	return year;
    }

    public void setYear(final String year) {
	this.year = year;
    }

    public String getAwardName() {
	return awardName;
    }

    public void setAwardName(final String awardName) {
	this.awardName = awardName;
    }
}
