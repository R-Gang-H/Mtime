package com.mtime.bussiness.ticket.movie.bean;

import com.mtime.base.bean.MBaseBean;

public class ActorImage extends MBaseBean {
    private int imageId;
    private String image;
    private int type;

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public String getImage() {
	return image;
    }

    public void setImage(final String image) {
	this.image = image;
    }

    public int getType() {
	return type;
    }

    public void setType(final int type) {
	this.type = type;
    }
}
