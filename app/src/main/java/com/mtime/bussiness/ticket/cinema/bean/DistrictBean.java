package com.mtime.bussiness.ticket.cinema.bean;

import com.mtime.base.bean.MBaseBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vivian.wei on 2017/10/17.
 * 影院列表页面筛选_城区
 */

public class DistrictBean extends MBaseBean {

    private int districtId;                                     // 城区id
    private String name;                                        // 城区名称
    private int cinemaCount;                                    // 附近影院数
    List<Integer> cinemaIds = new ArrayList<>();                // 影院Id数组
    List<BusinessAreaNewBean> businessAreas = new ArrayList<>();   // 商圈列表

    public int getDistrictId() {
        return districtId;
    }

    public void setDistrictId(int districtId) {
        this.districtId = districtId;
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

    public List<BusinessAreaNewBean> getBusinessAreas() {
        return businessAreas;
    }

    public void setBusinessAreas(List<BusinessAreaNewBean> businessAreas) {
        this.businessAreas = businessAreas;
    }
}
