package com.mtime.bussiness.ticket.movie.bean;

import com.mtime.base.bean.MBaseBean;

import java.io.Serializable;

public class CardListBean extends MBaseBean implements Serializable {
    private static final long serialVersionUID = 516237708041154691L;

    private String cId;
    private String cName;
    private String cNum;
    private int cType;
    private String balance;
    private String startTime;
    private String endTime;
    private String token;
    private boolean isSelected = true;
    private boolean used = false;
    
    public String getcId() {
        return cId;
    }
    
    public void setcId(String cId) {
        this.cId = cId;
    }
    
    public String getcName() {
        return cName;
    }
    
    public void setcName(String cName) {
        this.cName = cName;
    }
    
    public String getcNum() {
        return cNum;
    }
    
    public void setcNum(String cNum) {
        this.cNum = cNum;
    }
    
    public int getcType() {
        return cType;
    }
    
    public void setcType(int cType) {
        this.cType = cType;
    }
    
    public String getBalance() {
        return balance;
    }
    
    public void setBalance(String balance) {
        this.balance = balance;
    }
    
    public String getStartTime() {
        return startTime;
    }
    
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }
    
    public String getEndTime() {
        return endTime;
    }
    
    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
    
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public boolean isSelected() {
        return isSelected;
    }
    
    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }
    
    public boolean isUsed() {
        return used;
    }
    
    public void setUsed(boolean used) {
        this.used = used;
    }
    
}
