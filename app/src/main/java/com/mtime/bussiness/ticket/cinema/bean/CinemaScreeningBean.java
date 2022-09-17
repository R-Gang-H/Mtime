package com.mtime.bussiness.ticket.cinema.bean;

import com.mtime.base.bean.MBaseBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vivian.wei on 2017/10/17.
 * 影院列表页面筛选
 */

public class CinemaScreeningBean extends MBaseBean {

    private List<DistrictBean> districts = new ArrayList<>(); // 城区列表
    private List<SubwayBean> subways = new ArrayList<>();     // 地铁列表

    public List<DistrictBean> getDistricts() {
        return districts;
    }

    public void setDistricts(List<DistrictBean> districts) {
        this.districts = districts;
    }

    public List<SubwayBean> getSubways() {
        return subways;
    }

    public void setSubways(List<SubwayBean> subways) {
        this.subways = subways;
    }
}
