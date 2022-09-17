package com.mtime.beans;

import com.mtime.base.bean.MBaseBean;

public class ResultBean extends MBaseBean {
    /**
     * @return the msg
     */
    public String getMsg() {
	return msg;
    }

    /**
     * @param msg
     *            the msg to set
     */
    public void setMsg(final String msg) {
	this.msg = msg;
    }

    /**
     * @return the status
     */
    public int getStatus() {
	return status;
    }

    /**
     * @param status
     *            the status to set
     */
    public void setStatus(final int status) {
	this.status = status;
    }

    /**
     * @return the success
     */
    public boolean isSuccess() {
	return success;
    }

    /**
     * @param success
     *            the success to set
     */
    public void setSuccess(final boolean success) {
	this.success = success;
    }

    /**
     * @return the error
     */
    public String getError() {
	return error;
    }

    /**
     * @param error
     *            the error to set
     */
    public void setError(final String error) {
	this.error = error;
    }

    private boolean success;
    private String error;
    private String msg;
    private int status;
    private String headPic;
	public String getHeadPic() {
		return headPic;
	}

	public void setHeadPic(String headPic) {
		this.headPic = headPic;
	}
}
