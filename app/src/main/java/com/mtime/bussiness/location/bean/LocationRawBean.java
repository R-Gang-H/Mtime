package com.mtime.bussiness.location.bean;

import com.mtime.base.bean.MBaseBean;

import java.io.Serializable;

public class LocationRawBean extends MBaseBean implements Serializable {
    private static final long serialVersionUID = 1031438174618479784L;
    private boolean located = false;
    private double  mLatitude;
    private double  mLongitude;
    /**
     * 城市id
     */
    private String  cityId;
    /**
     * 城市名称
     */
    private String  name;
    
    /**
     * @return 是否成功定位标记
     */
    public boolean isLocated() {
        return located;
    }
    
    /**
     * @param located
     *            the located to set
     */
    public void setLocated(final boolean located) {
        this.located = located;
    }
    
    public String getCityId() {
        return cityId;
    }
    
    public void setCityId(final String cityId) {
        this.cityId = cityId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    /**
     * @return 经度
     */
    public double getLongitude() {
        return mLongitude;
    }
    
    /**
     * @param 设置经度
     */
    public void setLongitude(final double mLongitude) {
        this.mLongitude = mLongitude;
    }
    
    /**
     * @return 维度
     */
    public double getLatitude() {
        return mLatitude;
    }
    
    /**
     * @param 设置维度
     */
    public void setLatitude(final double mLatitude) {
        this.mLatitude = mLatitude;
    }
    
}
