package com.mtime.bussiness.ticket.cinema.bean;

import com.mtime.base.bean.MBaseBean;

/**
 * Created by yinguanping on 16/8/11.
 * 影院排片页优惠活动信息
 */
public class CinemaMoviesCouponActivityItem extends MBaseBean {

    private int activityId;//活动Id,正整数
    private boolean isSelected;//是否可选，bool型
    private String tag;//标签，string型
    private String desc;//简介，string型
    private String url;//url，string型
    private String descDetail;
    private int dsActivityFlag;//201805 是否为第三方直销影院准备的新类型票补活动 ； 1 的时候为直销影院。使用字段 tag/desc/descDetail。 0的时候为原有活动类型。

    public int getDsActivityFlag() {
        return dsActivityFlag;
    }

    public void setDsActivityFlag(int dsActivityFlag) {
        this.dsActivityFlag = dsActivityFlag;
    }

    public String getDescDetail() {
        return descDetail;
    }

    public void setDescDetail(String descDetail) {
        this.descDetail = descDetail;
    }

    public int getActivityId() {
        return activityId;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public void setActivityId(int activityId) {
        this.activityId = activityId;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(final String tag) {
        this.tag = tag;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(final String desc) {
        this.desc = desc;
    }

    public String getUrl() {
        if (url == null) {
            return "";
        }
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
