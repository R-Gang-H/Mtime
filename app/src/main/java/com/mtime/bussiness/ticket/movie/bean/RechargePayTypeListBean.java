package com.mtime.bussiness.ticket.movie.bean;

import com.mtime.base.bean.MBaseBean;

import java.io.Serializable;
import java.util.List;

public class RechargePayTypeListBean extends MBaseBean implements Serializable {
    private static final long serialVersionUID = 516840708041254691L;
    private int count;
    private List<ThirdPayListItem> cardList;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<ThirdPayListItem> getCardList() {
        return cardList;
    }

    public void setCardList(List<ThirdPayListItem> cardList) {
        this.cardList = cardList;
    }
}
