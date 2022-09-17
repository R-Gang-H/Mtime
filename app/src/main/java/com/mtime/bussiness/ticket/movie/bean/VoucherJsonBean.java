package com.mtime.bussiness.ticket.movie.bean;

import com.mtime.base.bean.MBaseBean;

import java.util.List;

/**
 * 优惠劵列表json实体（当订单要进行支付时候，需要获取，当前订单下，可以使用的优惠劵列表）
 * 
 * @author ye
 * 
 */
public class VoucherJsonBean extends MBaseBean {
    private double balance; // 帐户余额,单位分
    private String msg; // 失败信息，如：主订单状态不是新建，无法获取优惠劵列表
    private List<Voucher> voucherList;
   private List<CardListBean> cardList;
    public double getBalance() {
	return balance;
    }

    public void setBalance(final double balance) {
	this.balance = balance;
    }

    public String getMsg() {
	return msg;
    }

    public void setMsg(final String msg) {
	this.msg = msg;
    }

    public List<Voucher> getVoucherList() {
	return voucherList;
    }

    public void setVoucherList(final List<Voucher> voucherList) {
	this.voucherList = voucherList;
    }

    public List<CardListBean> getCardList()
    {
        return cardList;
    }

    public void setCardList(List<CardListBean> cardList)
    {
        this.cardList = cardList;
    }
}