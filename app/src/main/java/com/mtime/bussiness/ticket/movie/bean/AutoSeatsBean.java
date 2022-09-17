package com.mtime.bussiness.ticket.movie.bean;

import com.mtime.base.bean.MBaseBean;

import java.util.List;

public class AutoSeatsBean extends MBaseBean {
    private List<SeatColl> seatColl;
    private List<String> autoSeatIds;
    private int bizCode;
    private String msg;

    public List<SeatColl> getSeatColl() {
        return seatColl;
    }

    public void setSeatColl(List<SeatColl> seatColl) {
        this.seatColl = seatColl;
    }

    public int getBizCode() {
        return bizCode;
    }

    public void setBizCode(int bizCode) {
        this.bizCode = bizCode;
    }

    public String getMsg() {
        if (msg == null){
            return "";
        }
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<String> getAutoSeatIds() {
        return autoSeatIds;
    }

    public void setAutoSeatIds(List<String> autoSeatIds) {
        this.autoSeatIds = autoSeatIds;
    }
}
