package com.mtime.bussiness.ticket.movie.bean;

import com.mtime.base.bean.MBaseBean;

import java.util.List;

/**
 * Created by ZhangCong on 15/10/14.
 */
public class MovieSecretBean extends MBaseBean {
    private List<String> list;
    private String title;

    public List<String> getList() {
        return list;
    }

    public String getTitle() {
        return title;
    }

    public void setList(List<String> list) {
        this.list = list;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
