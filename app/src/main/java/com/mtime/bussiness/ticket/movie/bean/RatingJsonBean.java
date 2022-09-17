package com.mtime.bussiness.ticket.movie.bean;

import com.mtime.base.bean.MBaseBean;

/**
 * 提交评分后返回的数据
 */
public class RatingJsonBean extends MBaseBean {
    private String r; // 总评分
    private String rC; // 评分人数

    public String getR() {
	return r;
    }

    public void setR(final String r) {
	this.r = r;
    }

    public String getrC() {
	return rC;
    }

    public void setrC(final String rC) {
	this.rC = rC;
    }
}
