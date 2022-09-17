package com.mtime.bussiness.mine.bean;

import com.mtime.base.bean.MBaseBean;

/**
 * Created by LEE on 12/20/16.
 */

public class StatusBean  extends MBaseBean {
    private boolean success;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
