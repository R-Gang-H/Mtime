package com.mtime.bussiness.mine.profile.bean;


import com.mtime.base.bean.MBaseBean;

import java.util.List;

/**
 * Created by vivian.wei on 2017/4/11.
 * 个人资料_居住地列表Bean
 */

public class LocationListBean  extends MBaseBean {
    private List<LocationListItemBean> list;

    public List<LocationListItemBean> getList() {
        return list;
    }

    public void setList(List<LocationListItemBean> list) {
        this.list = list;
    }
}
