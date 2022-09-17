package com.mtime.bussiness.ticket.movie.bean;

import com.mtime.base.bean.MBaseBean;

import java.util.ArrayList;
import java.util.List;


public class V2_ShowTimesDateBean extends MBaseBean {
    private String                       date;
    private List<V2_ShowTimesByDateBean> showtimes = new ArrayList<V2_ShowTimesByDateBean>();
    
    public List<V2_ShowTimesByDateBean> getShowtimes() {
        return showtimes;
    }
    
    public void setShowtimes(List<V2_ShowTimesByDateBean> showtimes) {
        this.showtimes = showtimes;
    }
    
    public void setDate(final String date) {
        this.date = date;
    }
    
    public String getDate() {
        return date;
    }
    
    
}
