package com.jssdk.beans;

public class HandleTelBean {
    private DataBean data;
    
    public DataBean getData() {
        return data;
    }
    
    public void setData(DataBean data) {
        this.data = data;
    }
    
    public static class DataBean {
        public String phoneNumber;
    }
}
