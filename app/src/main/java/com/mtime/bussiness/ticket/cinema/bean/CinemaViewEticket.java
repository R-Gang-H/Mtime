package com.mtime.bussiness.ticket.cinema.bean;

import com.mtime.base.bean.MBaseBean;

import java.io.Serializable;

public class CinemaViewEticket extends MBaseBean implements Serializable {
    private static final long serialVersionUID = 2335638433059703557L;

    private int               eticketId;
    private String            name;
    private double            price;
    // 有效期模式，0-永久有效，1-相对时间有效，2-绝对时间有效
    private int               indateMode;
    // 有效期天数,当indateMode为1时有效
    private int               indateDays;
    // 开始时间,当indateMode为2时有效
    private long               startTime;
    // 结束时间,当indateMode为2时有效
    private long               endTime;

    public int getEticketId() {
        return eticketId;
    }

    public void setEticketId(int eticketId) {
        this.eticketId = eticketId;
    }

    public String getName() {
        return name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public double getPrice() {
        return price;
    }
    
    public void setPrice(final double price) {
        this.price = price;
    }

    public int getIndateMode() {
        return indateMode;
    }

    public void setIndateMode(int indateMode) {
        this.indateMode = indateMode;
    }

    public int getIndateDays() {
        return indateDays;
    }

    public void setIndateDays(int indateDays) {
        this.indateDays = indateDays;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }
}