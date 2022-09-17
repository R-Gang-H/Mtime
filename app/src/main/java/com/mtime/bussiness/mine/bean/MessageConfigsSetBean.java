package com.mtime.bussiness.mine.bean;

import com.mtime.base.bean.MBaseBean;

public class MessageConfigsSetBean  extends MBaseBean {
	private boolean success;
	private int status;
	private String error;

	public boolean getSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

}
