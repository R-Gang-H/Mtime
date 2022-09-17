package com.mtime.bussiness.ticket.movie.bean;

import android.text.TextUtils;

import com.mtime.base.bean.MBaseBean;

public class ActorFestivals extends MBaseBean {

    private int festivalId;
    private String img;
    private String nameCn;
    private String shortName;
    private String nameEn;

    public int getFestivalId() {
        return festivalId;
    }

    public void setFestivalId(int festivalId) {
        this.festivalId = festivalId;
    }

    public String getNameCn() {
        return nameCn;
    }

    public void setNameCn(String nameCn) {
        this.nameCn = nameCn;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getNameEn() {
        return nameEn;
    }

    public void setNameEn(String nameEn) {
        this.nameEn = nameEn;
    }

    public boolean isEmpty() {
        return 0 == festivalId && TextUtils.isEmpty(nameCn) && TextUtils.isEmpty(nameEn);
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}
