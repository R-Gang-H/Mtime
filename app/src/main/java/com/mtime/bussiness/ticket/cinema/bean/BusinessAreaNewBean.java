package com.mtime.bussiness.ticket.cinema.bean;

import com.mtime.base.bean.MBaseBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vivian.wei on 2017/10/17.
 * 影院列表页面筛选_商圈
 */

public class BusinessAreaNewBean extends MBaseBean {

     private int businessId;    // 商圈Id
     private String name;       // 名称
     private int cinemaCount;   // 附近影院数
     private List<Integer> cinemaIds = new ArrayList<>(); // 影院Id数组

    public int getBusinessId() {
        return businessId;
    }

    public void setBusinessId(int businessId) {
        this.businessId = businessId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCinemaCount() {
        return cinemaCount;
    }

    public void setCinemaCount(int cinemaCount) {
        this.cinemaCount = cinemaCount;
    }

    public List<Integer> getCinemaIds() {
        return cinemaIds;
    }

    public void setCinemaIds(List<Integer> cinemaIds) {
        this.cinemaIds = cinemaIds;
    }
}
