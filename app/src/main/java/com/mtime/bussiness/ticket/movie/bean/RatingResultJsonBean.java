package com.mtime.bussiness.ticket.movie.bean;

import com.mtime.base.bean.MBaseBean;

import java.util.List;

/**
 * 提交评分后返回的数据
 */
public class RatingResultJsonBean extends MBaseBean {
    private String error; // 错误信息: 当发生错误时，会输出提示信息。
    private RatingJsonBean r; // 评分信息，当发生错误时，此项为null。
    private List<String> Imgs;
    public String getError() {
	return error;
    }

    public void setError(final String error) {
	this.error = error;
    }

    public RatingJsonBean getR() {
	return r;
    }

    public void setR(final RatingJsonBean r) {
	this.r = r;
    }

    public List<String> getImgs() {
        return Imgs;
    }

    public void setImgs(final List<String> imgs) {
        this.Imgs= imgs;
    }
}
