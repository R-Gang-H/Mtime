package com.mtime.bussiness.ticket.cinema.bean;

import com.mtime.base.bean.MBaseBean;

public class SyncFavoriteCinemaBean extends MBaseBean {
    private boolean success;          // 是否同步成功
    private String  error;            // 错误描述，只有success为false返回。
    private int     errorCode;        // 错误码，-1
                                       // 表示添加删除收藏失败，-2添加收藏失败、-3删除收藏失败、-4没有登录、-5
                                       // 添加删除数量不能超过40,、-6 没有同步的数据。
    private String  addErrorIdList;   // 添加收藏失败的影院Id组，用”,”隔开。
    private String  cinemaIdFaillList; // 添加收藏失败使用非法的影院Id组，用”,”隔开。
                                       
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public String getError() {
        if (error == null) {
            return "";
        }
        return error;
    }
    
    public void setError(String error) {
        this.error = error;
    }
    
    public int getErrorCode() {
        return errorCode;
    }
    
    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }
    
    public String getAddErrorIdList() {
        if (addErrorIdList == null) {
            return "";
        }
        return addErrorIdList;
    }
    
    public void setAddErrorIdList(String addErrorIdList) {
        this.addErrorIdList = addErrorIdList;
    }
    
    public String getCinemaIdFaillList() {
        if (cinemaIdFaillList == null) {
            return "";
        }
        return cinemaIdFaillList;
    }
    
    public void setCinemaIdFaillList(String cinemaIdFaillList) {
        this.cinemaIdFaillList = cinemaIdFaillList;
    }
    
}
