package com.mtime.bussiness.ticket.cinema.bean;

import com.mtime.base.bean.MBaseBean;

public class CheckIsFavoriteBean extends MBaseBean {
    // “success”: true,
    // “error” : “type参数错误”//此属性，只有当success为false时，才会有
    // “favoriteID”: 0 //此属性只有当success为true时候，才会有，如果为为0，说明没有收藏，如果为1则已经收藏了
    private boolean success;
    private String error;
    private int favoriteID;
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
    public int getFavoriteID() {
        return favoriteID;
    }
    public void setFavoriteID(int favoriteID) {
        this.favoriteID = favoriteID;
    }
    
}
