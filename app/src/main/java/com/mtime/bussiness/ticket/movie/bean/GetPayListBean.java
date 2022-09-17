package com.mtime.bussiness.ticket.movie.bean;

import com.mtime.base.bean.MBaseBean;

import java.util.ArrayList;

public class GetPayListBean extends MBaseBean {
    private boolean success;
    private String error;
    private ArrayList<PayCardListBean> cardList;
    public boolean isSuccess() {
        return success;
    }
    public void setSuccess(boolean success) {
        this.success = success;
    }
    public ArrayList<PayCardListBean> getCardList() {
        return cardList;
    }
    public void setCardList(ArrayList<PayCardListBean> cardList) {
        this.cardList = cardList;
    }
    public String getError() {
        return error;
    }
    public void setError(String error) {
        this.error = error;
    }
    
}
