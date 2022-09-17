package com.mtime.bussiness.ticket.movie.bean;

import com.mtime.base.bean.MBaseBean;

/**
 * Created by guangshun on 16/8/8.
 */
public class WinAwardsPerson extends MBaseBean {
    int personId;
    String nameCn;
    String nameEn;

    public void setPersonId(int personId) {
        this.personId = personId;
    }

    public void setNameCn(String nameCn) {
        this.nameCn = nameCn;
    }

    public void setNameEn(String nameEn) {
        this.nameEn = nameEn;
    }

    public String getNameCn() {
        return nameCn;
    }

    public String getNameEn() {
        return nameEn;
    }
}
