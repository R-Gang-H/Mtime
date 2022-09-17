package com.mtime.bussiness.location.bean;

import com.mtime.base.bean.MBaseBean;

import java.util.ArrayList;

public class ChangeCitySortBean extends MBaseBean {
    private ArrayList<CityBean> cityBeans = new ArrayList<CityBean>();
    private String pinYinTitle;

    public ArrayList<CityBean> getCityBeans() {
	return cityBeans;
    }

    public void setCityBeans(final ArrayList<CityBean> cityBeans) {
	this.cityBeans = cityBeans;
    }

    public String getPinYinTitle() {
	return pinYinTitle;
    }

    public void setPinYinTitle(final String pinYinTitle) {
	this.pinYinTitle = pinYinTitle;
    }

}
