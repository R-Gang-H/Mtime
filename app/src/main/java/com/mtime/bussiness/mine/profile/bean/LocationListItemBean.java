package com.mtime.bussiness.mine.profile.bean;

import com.mtime.base.bean.MBaseBean;

/**
 * Created by vivian.wei on 2017/4/11.
 * 个人资料_居住地列表单项Bean
 */

public class LocationListItemBean  extends MBaseBean {
    private int locationId;      // 290
    private String locationName; // 北京
    private boolean isSubset;    // 是否有子集数据
    private boolean isSelect;    // 是否已选地区

    public int getLocationId() {
        return locationId;
    }

    public void setLocationId(int locationId) {
        this.locationId = locationId;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public boolean isSubset() {
        return isSubset;
    }

    public void setSubset(boolean subset) {
        isSubset = subset;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }
}
