package com.mtime.bussiness.mine.profile.bean;

import com.mtime.base.bean.MBaseBean;

/**
 * 修改昵称bean
 * 
 * @author Bruce.Bao
 * 
 */
public class ChangeNicknameBean  extends MBaseBean {

    private boolean success;
    private String  error;

    public boolean getSuccess() {
        return success;
    }

    public void setSuccess(final boolean success) {
        this.success = success;
    }

    public String getErrorMessage() {
        return error;
    }

    public void setErrorMessage(final String errorMessage) {
        this.error = errorMessage;
    }
}