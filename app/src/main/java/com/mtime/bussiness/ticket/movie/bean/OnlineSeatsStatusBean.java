package com.mtime.bussiness.ticket.movie.bean;

import com.mtime.base.bean.MBaseBean;

import java.util.List;

public class OnlineSeatsStatusBean  extends MBaseBean {
    private List<OnlineSeatsStatusSelectedBean> selected;
    private List<OnlineSeatsStatusRowSeatBean> rowSeats;

    public List<OnlineSeatsStatusSelectedBean> getSelected() {
        return selected;
    }

    public void setSelected(List<OnlineSeatsStatusSelectedBean> selected) {
        this.selected = selected;
    }

    public List<OnlineSeatsStatusRowSeatBean> getRowSeats() {
        return rowSeats;
    }

    public void setRowSeats(List<OnlineSeatsStatusRowSeatBean> rowSeats) {
        this.rowSeats = rowSeats;
    }

}
