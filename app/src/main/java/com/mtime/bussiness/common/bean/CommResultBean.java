package com.mtime.bussiness.common.bean;

import com.mtime.base.bean.MBaseBean;

/**
 * 通用的请求返回结果
 */
public class CommResultBean extends MBaseBean {
    private boolean success;
    private String error; // success = true时此处为null

    public boolean isSuccess() {
	return success;
    }

    public void setSuccess(final boolean success) {
	this.success = success;
    }

    public String getError() {
	return error;
    }

    public void setError(final String error) {
	this.error = error;
    }
}
