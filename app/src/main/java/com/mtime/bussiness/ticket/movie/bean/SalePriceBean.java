package com.mtime.bussiness.ticket.movie.bean;

import com.mtime.base.bean.MBaseBean;

import java.io.Serializable;

public class SalePriceBean extends MBaseBean implements Serializable {
    private static final long serialVersionUID = 516840708041254691L;
    private double ticketCount;
    private double saleAmount;
    private double serviceFee;
    private String amountFormat;

    public double getTicketCount() {
        return ticketCount;
    }
    public void setTicketCount(double ticketCount) {
        this.ticketCount = ticketCount;
    }
    public double getSaleAmount() {
        return saleAmount;
    }
    public void setSaleAmount(double saleAmount) {
        this.saleAmount = saleAmount;
    }
    public double getServiceFee() {
        return serviceFee;
    }
    public void setServiceFee(double serviceFee) {
        this.serviceFee = serviceFee;
    }

    public String getAmountFormat() {
        return amountFormat;
    }

    public void setAmountFormat(String amountFormat) {
        this.amountFormat = amountFormat;
    }
}
