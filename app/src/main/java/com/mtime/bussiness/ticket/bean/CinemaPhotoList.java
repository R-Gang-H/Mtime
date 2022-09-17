package com.mtime.bussiness.ticket.bean;

import com.mtime.base.bean.MBaseBean;

import java.io.Serializable;
import java.util.ArrayList;

public class CinemaPhotoList extends MBaseBean implements Serializable {
    private static final long serialVersionUID = 2031438174618479784L;
    private ArrayList<CinemaPhoto> galleryList;

    public ArrayList<CinemaPhoto> getGalleryList() {
	return galleryList;
    }

    public void setGalleryList(final ArrayList<CinemaPhoto> galleryList) {
	this.galleryList = galleryList;
    }
}
