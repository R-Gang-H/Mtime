package com.mtime.bussiness.ticket.cinema.bean;

import com.mtime.base.bean.MBaseBean;

/**
 * Created by guangshun on 15-6-3.
 */
public class CouponActivityItem extends MBaseBean {
    private String id; //活动Id
    private boolean isSelected; //是否可选
    private String tag; //标签
    private String desc; //简介
    private String url;
    private String descDetail;  // 为第三方直销影院活动新增的字段，对于活动的详细描述
    private int dsActivityFlag; // 是否为第三方直销影院准备的新类型票补活动 ； 1 的时候为直销影院 0的时候为原有活动类型

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
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

    public boolean getIsSelected() {
        return isSelected;
    }

    public void setIsSelected(final boolean isSelected) {
        this.isSelected = isSelected;
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

    public String getDescDetail() {
        return descDetail;
    }

    public void setDescDetail(String descDetail) {
        this.descDetail = descDetail;
    }

    public int getDsActivityFlag() {
        return dsActivityFlag;
    }

    public void setDsActivityFlag(int dsActivityFlag) {
        this.dsActivityFlag = dsActivityFlag;
    }
}
