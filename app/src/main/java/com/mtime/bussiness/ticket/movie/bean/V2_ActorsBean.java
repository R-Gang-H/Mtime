package com.mtime.bussiness.ticket.movie.bean;

import com.mtime.base.bean.MBaseBean;

import java.io.Serializable;


public class V2_ActorsBean extends MBaseBean implements Serializable {
	/**
     * 
     */
    private static final long serialVersionUID = 5544241296565807192L;
    private int actorId;
	private String name;
	private String nameEn;
	private String img;
	private String roleName;
	private String roleImg;

	public void setactorId(final int actorId) {
		this.actorId = actorId;
	}

	public int getactorId() {
		return actorId;
	}

	public void setroleName(final String roleName) {
		this.roleName = roleName;
	}

	public String getroleName() {
		return roleName;
	}

	public void setroleImg(final String roleImg) {
		this.roleImg = roleImg;
	}

	public String getroleImg() {
		return roleImg;
	}

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNameEn() {
		return nameEn;
	}

	public void setNameEn(String nameEn) {
		this.nameEn = nameEn;
	}

	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img;
	}
}
