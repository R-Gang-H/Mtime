package com.mtime.bussiness.ticket.movie.bean;

import android.text.TextUtils;

import com.mtime.base.bean.MBaseBean;

import java.io.Serializable;

public class V2_DirectorBean extends MBaseBean implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 2808524804494127002L;
    private int directorId;
    private String name;
    private String nameEn;
    private String img;

    public int getdirectorId() {
        return directorId;
    }

    public void setcId(int directorId) {
        this.directorId = directorId;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public int getDirectorId() {
        return directorId;
    }

    public void setDirectorId(int directorId) {
        this.directorId = directorId;
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
    public boolean isEmpty(){
        return 0 == directorId && TextUtils.isEmpty(name) && TextUtils.isEmpty(nameEn) && TextUtils.isEmpty(img);
    }
}
