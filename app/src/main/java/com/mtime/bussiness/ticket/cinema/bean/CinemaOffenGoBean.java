/**
 * 
 */
package com.mtime.bussiness.ticket.cinema.bean;

import com.mtime.base.bean.MBaseBean;

import java.io.Serializable;

/**
 * @author wangjin
 * 
 */
public class CinemaOffenGoBean extends MBaseBean implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = -3850822895913834043L;
    private int id;
    private String name;
    private String adress;
    private int favoriteId = 0;

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

    public String getAdress() {
	return adress;
    }

    public void setAdress(final String adress) {
	this.adress = adress;
    }

    public int getFavoriteId() {
        return favoriteId;
    }

    public void setFavoriteId(int favoriteId) {
        this.favoriteId = favoriteId;
    }

}
