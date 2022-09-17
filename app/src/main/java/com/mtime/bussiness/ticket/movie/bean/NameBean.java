package com.mtime.bussiness.ticket.movie.bean;

import com.mtime.base.bean.MBaseBean;

/**
 * 作品年表-FilmographyBean用到的bean,根据服务端接口返回的数据格式，得这样定义实体bean
 * 
 * @author ye
 * 
 */
public class NameBean extends MBaseBean {
    private String name;

    public String getName() {
	return name;
    }

    public void setName(final String name) {
	this.name = name;
    }
}
