package com.mtime.bussiness.ticket.movie.bean;

import com.mtime.base.bean.MBaseBean;

public class AnonymousPayBean extends MBaseBean {

    private boolean success;
    private String msg;
    private int status;
    private String rechargeNumber;
    private String formXML;
    public boolean isSuccess() {
	return success;
    }

    public void setSuccess(final boolean success) {
	this.success = success;
    }

    public String getMsg() {
	return msg;
    }

    public void setMsg(final String msg) {
	this.msg = msg;
    }

    public int getStatus() {
	return status;
    }

    public void setStatus(final int status) {
	this.status = status;
    }

	public String getRechargeNumber() {
		return rechargeNumber;
	}

	public void setRechargeNumber(String rechargeNumber) {
		this.rechargeNumber = rechargeNumber;
	}

	public String getFormXML() {
		return formXML;
	}

	public void setFormXML(String formXML) {
		this.formXML = formXML;
	}

}
