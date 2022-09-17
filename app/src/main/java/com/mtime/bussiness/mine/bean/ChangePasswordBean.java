package com.mtime.bussiness.mine.bean;

import com.mtime.base.bean.MBaseBean;

/**
 * 修改密码bean
 * 
 * @author Bruce.Bao
 * 
 */
public class ChangePasswordBean  extends MBaseBean {

    private boolean    success;
    private int    status;
    private String error;

    public boolean getSuccess() {
        return success;
    }

    public void setSuccess(final boolean success) {
        this.success = success;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(final int status) {
        this.status = status;
    }

    public String getErrorMessage() {
        return error;
    }

    public void setErrorMessage(final String error) {
        this.error = error;
    }
}