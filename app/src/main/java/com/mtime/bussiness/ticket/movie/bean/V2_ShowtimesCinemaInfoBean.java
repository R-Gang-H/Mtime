package com.mtime.bussiness.ticket.movie.bean;


import com.mtime.base.bean.MBaseBean;

public class V2_ShowtimesCinemaInfoBean  extends MBaseBean {
    private int Id;
    private String name;

    public void setId(final int Id) {
        this.Id = Id;
    }

    public int getId() {
        return Id;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
