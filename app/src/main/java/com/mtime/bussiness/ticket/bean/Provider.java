package com.mtime.bussiness.ticket.bean;

import com.mtime.base.bean.MBaseBean;

import java.io.Serializable;

/**
 * 供应商
 */
public class Provider extends MBaseBean implements Serializable {
    private static final long serialVersionUID = -3635153062783897145L;
    private Long id;                                      // 供应商id，
    private int dId;                                     // 场次id
    private String level;//优先级(数字越大优先级越高)
    private long pId;
    private String dsShowtimeId; //第三方直销场次ID，用于第三方跳转。因为是String型，和原有的sId不一样，新设字段
    private int directSalesFlag; //是否是第三方直销// 供应商id（兼容不同接口定义字段名不同）

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getDsShowtimeId() {
        return dsShowtimeId;
    }

    public void setDsShowtimeId(String dsShowtimeId) {
        this.dsShowtimeId = dsShowtimeId;
    }

    public int getDirectSalesFlag() {
        return directSalesFlag;
    }

    public void setDirectSalesFlag(int directSalesFlag) {
        this.directSalesFlag = directSalesFlag;
    }

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public int getdId() {
        return dId;
    }

    public void setdId(final int dId) {
        this.dId = dId;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public long getpId() {
        return pId;
    }

    public void setpId(long pId) {
        this.pId = pId;
    }
}
