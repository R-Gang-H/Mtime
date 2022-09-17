package com.mtime.bussiness.ticket.movie.bean;

import com.mtime.base.bean.MBaseBean;

public class IncomingTopBean extends MBaseBean {
    /**
     * @return the id
     */
    public String getId() {
	return id;
    }

    /**
     * @param id
     *            the id to set
     */
    public void setId(final String id) {
	this.id = id;
    }

    /**
     * @return the title
     */
    public String getTitle() {
	return title;
    }

    /**
     * @param title
     *            the title to set
     */
    public void setTitle(final String title) {
	this.title = title;
    }

    /**
     * @return the image
     */
    public String getImage() {
	return image;
    }

    /**
     * @param image
     *            the image to set
     */
    public void setImage(final String image) {
	this.image = image;
    }

    /**
     * @return the releaseDate
     */
    public String getReleaseDate() {
	return releaseDate;
    }

    /**
     * @param releaseDate
     *            the releaseDate to set
     */
    public void setReleaseDate(final String releaseDate) {
	this.releaseDate = releaseDate;
    }

    /**
     * @return the wantToSeeCount
     */
    public int getWantToSeeCount() {
	return wantToSeeCount;
    }

    /**
     * @param wantToSeeCount
     *            the wantToSeeCount to set
     */
    public void setWantToSeeCount(final int wantToSeeCount) {
	this.wantToSeeCount = wantToSeeCount;
    }

    // {"id":30876,"title":"深海寻人","image":"http://img31.test.cn/mg/2012/05/31/121207.94408729.jpg","releaseDate":"2011-10-28","wantToSeeCount":13}
    private String id;
    private String title;
    private String image;
    private String releaseDate;
    private int wantToSeeCount;
}
