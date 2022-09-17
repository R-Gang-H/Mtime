package com.mtime.beans;

import com.mtime.base.bean.MBaseBean;

/**
 * Created by LEE on 15-4-15.
 */
public class IsLoginResultBean extends MBaseBean {
    private boolean success;
    private String error;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
