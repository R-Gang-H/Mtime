package com.mtime.bussiness.splash.bean;

import com.mtime.base.bean.MBaseBean;

import java.util.List;

public class SplashSeatsIconList extends MBaseBean
{
    private List<SplashSeatsIconBean> list;
    private String tag;

    public List<SplashSeatsIconBean> getList() {
        return list;
    }

    public void setList(List<SplashSeatsIconBean> list) {
        this.list = list;
    }

    public String getTag() {
        if (tag == null) {
            return "";
        } else {
            return tag;
        }
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
