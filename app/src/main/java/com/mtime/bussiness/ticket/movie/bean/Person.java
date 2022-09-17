package com.mtime.bussiness.ticket.movie.bean;

import com.mtime.base.bean.MBaseBean;

public class Person extends MBaseBean {
    private int id;
    private String name;
    private String nameEn;
    private String image;
    private String personate;
    private String roleCover;

    // 自定义字段
    private boolean isGroupEnd;

    public int getId() {
	return id;
    }

    public void setId(final int id) {
	this.id = id;
    }

    public String getName() {
	return name;
    }

    public void setName(final String name) {
	this.name = name;
    }

    public String getNameEn() {
	return nameEn;
    }

    public void setNameEn(final String nameEn) {
	this.nameEn = nameEn;
    }

    public String getRoleCover() {
	return roleCover;
    }

    public void setRoleCover(final String roleCover) {
	this.roleCover = roleCover;
    }
    
    public String getImage() {
    	return image;
        }

    public void setImage(final String image) {
    	this.image = image;
        }

    public String getPersonate() {
	return personate;
    }

    public void setPersonate(final String personate) {
	this.personate = personate;
    }

    public boolean isGroupEnd() {
        return isGroupEnd;
    }

    public void setGroupEnd(boolean groupEnd) {
        isGroupEnd = groupEnd;
    }
}
