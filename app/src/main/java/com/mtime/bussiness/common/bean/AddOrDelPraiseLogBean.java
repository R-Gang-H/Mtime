package com.mtime.bussiness.common.bean;

import com.mtime.base.bean.MBaseBean;

public class AddOrDelPraiseLogBean extends MBaseBean {
    private boolean success;
    private String  error;
    private int     totalCount;
    private boolean isAdd;
    
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
    
    public int getTotalCount() {
        return totalCount;
    }
    
    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
    
    public boolean isAdd() {
        return isAdd;
    }
    
    public void setAdd(boolean isAdd) {
        this.isAdd = isAdd;
    }
    
}
