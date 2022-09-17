package com.mtime.bussiness.ticket.movie.bean;

import com.helen.obfuscator.IObfuscateKeepAll;

import java.util.List;

/**
 * 影人作品列表Bean
 */
public class FilmographyListBean implements IObfuscateKeepAll {

    private List<FilmographyBean> list; // 影人作品列表

    public List<FilmographyBean> getList() {
        return list;
    }

    public void setList(List<FilmographyBean> list) {
        this.list = list;
    }
}
