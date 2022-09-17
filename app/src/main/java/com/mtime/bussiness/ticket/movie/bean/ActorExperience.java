package com.mtime.bussiness.ticket.movie.bean;

import android.text.TextUtils;

import com.mtime.base.bean.MBaseBean;

public class ActorExperience  extends MBaseBean {

    private String title;
    private int year;
    private String content;
    private String img;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public boolean isEmpty() {
        return TextUtils.isEmpty(title) && year == 0 && TextUtils.isEmpty(content) && TextUtils.isEmpty(img);
    }
}
