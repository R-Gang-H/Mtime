package com.mtime.bussiness.location.bean;

import com.mtime.base.bean.MBaseBean;
import com.mtime.bussiness.location.CityChangeActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * 省（包括直辖市）
 * 
 * @author ye 2012-09-29
 * 用于{@link CityChangeActivity}
 */
public class ProvinceBean extends MBaseBean {

    private int count;

    private String pinyinFull;
    // 省（包括直辖市）编号
    private int id;
    // 省（包括直辖市）名称
    private String n;
    private String pinyinShort;
    // 城市列表

    private List<CityBean> c = new ArrayList<CityBean>();

    public ProvinceBean() {
    }

    public ProvinceBean(final int id, final String name) {
	this.id = id;
	n = name;
    }

    public int getId() {
	return id;
    }

    public void setId(final int id) {
	this.id = id;
    }

    public String getName() {
	return n;
    }

    public void setN(final String name) {
	n = name;
    }

    public List<CityBean> getCities() {
	return c;
    }

    public void setC(final List<CityBean> cities) {
	c = cities;
    }

    public String getPinyinShort() {
	return pinyinShort;
    }

    public void setPinyinShort(final String pinyinShort) {
	this.pinyinShort = pinyinShort;
    }

    /**
     *
     */
    public String getPinyinFull() {
	return pinyinFull;
    }

    /**
     *
     */
    public void setPinyinFull(final String pinyinFull) {
	this.pinyinFull = pinyinFull;
    }

    /**
     *
     */
    public int getCount() {
	return count;
    }

    /**
     *
     */
    public void setCount(final int count) {
	this.count = count;
    }
}
