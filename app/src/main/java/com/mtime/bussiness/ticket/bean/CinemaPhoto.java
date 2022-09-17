package com.mtime.bussiness.ticket.bean;

import com.mtime.base.bean.MBaseBean;

import java.io.Serializable;

/**
 * 影院图集实体；
 * 用于图片列表
 * @author ye
 * 
 */
public class CinemaPhoto extends MBaseBean implements Serializable {
    private static final long serialVersionUID = -1419994717437192044L;

    private String imageId;
    private String imageUrl;
    private String imageType; // 图片类型:
			      // 景观=1，影厅=2，活动=3，其他=4

    public String getImageId() {
	return imageId;
    }

    public void setImageId(final String imageId) {
	this.imageId = imageId;
    }

    public String getImageUrl() {
	return imageUrl;
    }

    public void setImageUrl(final String imageUrl) {
	this.imageUrl = imageUrl;
    }

    public String getImageType() {
	return imageType;
    }

    public void setImageType(final String imageType) {
	this.imageType = imageType;
    }
}
