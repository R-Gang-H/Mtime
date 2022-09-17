package com.mtime.bussiness.mine.bean;

import com.mtime.base.bean.MBaseBean;

/**
 * Created by vivian.wei on 2017/4/5.
 * 个人账户-居住地
 */

public class AccountLocationBean  extends MBaseBean {
    private int locationId;
    private String locationName;
    private String levelRelation;  // 20-2-290  层级关系  国家id-省id-城市id

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

    public String getLevelRelation() {
        return levelRelation;
    }

    public void setLevelRelation(String levelRelation) {
        this.levelRelation = levelRelation;
    }
}