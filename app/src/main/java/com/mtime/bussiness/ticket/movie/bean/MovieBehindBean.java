package com.mtime.bussiness.ticket.movie.bean;

import com.mtime.base.bean.MBaseBean;

/**
 * Created by mtime on 15/10/16.
 */
//TODO 等待具体接口出来，可能会修改
public class MovieBehindBean extends MBaseBean {
    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
