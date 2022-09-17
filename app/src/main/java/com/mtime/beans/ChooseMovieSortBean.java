package com.mtime.beans;

import com.mtime.base.bean.MBaseBean;

import java.io.Serializable;

public class ChooseMovieSortBean extends MBaseBean implements Serializable {
    private String name;
    //热度 0, 评分1,年份2,
    private int sortType;
    // false 倒序 ， true 正序
    private boolean sortMethod;

    public String getName() {
        if (name == null) {
            return "";
        }
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSortType() {
        return sortType;
    }

    public void setSortType(int sortType) {
        this.sortType = sortType;
    }

    public boolean isSortMethod() {
        return sortMethod;
    }

    public void setSortMethod(boolean sortMethod) {
        this.sortMethod = sortMethod;
    }
}
