package com.mtime.bussiness.ticket.movie.bean;

import com.mtime.base.bean.MBaseBean;

public class ShowtimeDate extends MBaseBean {

    private String dateValue;
    private Long seconds;

    public String getDateValue() {
	return dateValue;
    }

    public void setDateValue(final String dateValue) {
	this.dateValue = dateValue;
    }

    public Long getSeconds() {
	return seconds;
    }

    public void setSeconds(final Long seconds) {
	this.seconds = seconds;
    }
}
